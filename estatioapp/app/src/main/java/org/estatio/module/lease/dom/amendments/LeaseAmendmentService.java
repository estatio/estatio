package org.estatio.module.lease.dom.amendments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSource;
import org.estatio.module.lease.dom.LeaseItemSourceRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.lease.dom.amendments.LeaseAmendmentService"
)
public class LeaseAmendmentService {

    public static Logger LOG = LoggerFactory.getLogger(LeaseAmendmentService.class);

    public void apply(final LeaseAmendment leaseAmendment, final boolean preview) {

        // Only implementation for the moment
        if (!Arrays.asList(
                LeaseAmendmentType.COVID_FRA_50_PERC,
                LeaseAmendmentType.COVID_FRA_100_PERC,
                LeaseAmendmentType.DEMO_TYPE).
                contains(leaseAmendment.getLeaseAmendmentType())
        ) {
            messageService.warnUser(String.format("Amendment type %s is not implemented (yet...)", leaseAmendment.getLeaseAmendmentType()));
            return;
        }

        final Lease lease = preview ? leaseAmendment.getLeasePreview() : leaseAmendment.getLease();
        final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount = Lists
                .newArrayList(leaseAmendment.getItems()).stream()
                .filter(lai -> lai.getClass().isAssignableFrom(LeaseAmendmentItemForDiscount.class))
                .map(LeaseAmendmentItemForDiscount.class::cast)
                .findFirst().orElse(null);
        // NOTE: we apply discount first, before frequency change, because it copies the original invoice frequnecy ...
        if (leaseAmendmentItemForDiscount!=null){
            applyDiscount(lease, leaseAmendmentItemForDiscount);
        }
        final LeaseAmendmentItemForFrequencyChange leaseAmendmentItemForFrequencyChange = Lists
                .newArrayList(leaseAmendment.getItems()).stream()
                .filter(lai -> lai.getClass().isAssignableFrom(LeaseAmendmentItemForFrequencyChange.class))
                .map(LeaseAmendmentItemForFrequencyChange.class::cast)
                .findFirst().orElse(null);
        if (leaseAmendmentItemForFrequencyChange!=null){
            applyFrequencyChange(lease, leaseAmendmentItemForFrequencyChange);
        }
        if (!preview) {
            leaseAmendment.setState(LeaseAmendmentState.APPLIED);
        }
    }

    void applyDiscount(
            final Lease lease,
            final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount) {
        // find lease items included in discount
        final List<LeaseItem> itemsToIncludeForDiscount = Lists.newArrayList(lease.getItems()).stream()
                .filter(li -> LeaseAmendmentItem
                        .applicableToFromString(leaseAmendmentItemForDiscount.getApplicableTo())
                        .contains(li.getType()))
                .filter(li->li.getEffectiveInterval().overlaps(leaseAmendmentItemForDiscount.getInterval()))
                .collect(Collectors.toList());
        for (LeaseItem li : itemsToIncludeForDiscount){
            createDiscountItemAndTerms(li, leaseAmendmentItemForDiscount);
        }
    }

    void createDiscountItemAndTerms(LeaseItem sourceItem, final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount){

        LocalDate startDateToUse = sourceItem.getStartDate()==null || sourceItem.getStartDate().isBefore(leaseAmendmentItemForDiscount.getStartDate()) ? leaseAmendmentItemForDiscount.getStartDate() : sourceItem.getStartDate();
        LocalDate endDateToUse = sourceItem.getEndDate()==null || sourceItem.getEndDate().isAfter(leaseAmendmentItemForDiscount.getEndDate()) ? leaseAmendmentItemForDiscount.getEndDate() : sourceItem.getEndDate();
        Lease lease = sourceItem.getLease();
        final Charge chargeFromAmendmentType = chargeRepository.findByReference(
                leaseAmendmentItemForDiscount.getLeaseAmendment().getLeaseAmendmentType()
                        .getChargeReferenceForDiscountItem());
        final Charge chargeToUse = chargeFromAmendmentType != null ? chargeFromAmendmentType : sourceItem.getCharge(); // This is a fallback. for testing f.i.
        final LeaseItem newDiscountItem = lease
                .newItem(sourceItem.getType(), sourceItem.getInvoicedBy(), chargeToUse, sourceItem.getInvoicingFrequency(), sourceItem.getPaymentMethod(), startDateToUse);
        newDiscountItem.setEndDate(endDateToUse);
        sourceItem.copyTerms(newDiscountItem.getStartDate(), newDiscountItem);
        newDiscountItem.negateAmountsAndApplyPercentageOnTerms(leaseAmendmentItemForDiscount.getDiscountPercentage());
        // TODO: apply percentage
    }

    void applyFrequencyChange(final Lease lease,
            final LeaseAmendmentItemForFrequencyChange leaseAmendmentItemForFrequencyChange){
        for (LeaseItemType leaseItemType : LeaseAmendmentItem.applicableToFromString(leaseAmendmentItemForFrequencyChange.getApplicableTo())){
            switch (leaseItemType){
            case RENT:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.RENT, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getEndDate().plusDays(1), LeaseItemType.RENT, leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), false);
                break;
            case SERVICE_CHARGE:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.SERVICE_CHARGE, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getEndDate().plusDays(1), LeaseItemType.SERVICE_CHARGE, leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), false);
                break;
            case SERVICE_CHARGE_INDEXABLE:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.SERVICE_CHARGE_INDEXABLE, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getEndDate().plusDays(1), LeaseItemType.SERVICE_CHARGE_INDEXABLE, leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), false);
                break;
            case MARKETING:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.MARKETING, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getEndDate().plusDays(1), LeaseItemType.MARKETING, leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), false);
                break;
            default:
                final String warning = String
                        .format("Applying frequency change on lease %s for type %s is not (yet) supported",
                                lease.getReference(), leaseItemType);
                messageService.warnUser(
                        warning);
                LOG.error(warning);
                break;
            }
        }
    }

    public Lease getLeasePreviewFor(final LeaseAmendment amendment){
        Lease leasePreview = getCopyForPreview(amendment);
        amendment.setLeasePreview(leasePreview);
        apply(amendment, true);
        return leasePreview;
    }

    Lease getCopyForPreview(final LeaseAmendment amendment){
        Lease orginalLease = amendment.getLease();
        LocalDate referenceDate = amendment.getEffectiveStartDate();
        orginalLease.verifyUntil(amendment.getEffectiveEndDate());

        Lease leaseCopy = new Lease();
        leaseCopy.setStatus(LeaseStatus.PREVIEW);
        leaseCopy.setReference(amendment.getReference());
        leaseCopy.setStartDate(orginalLease.getStartDate());
        leaseCopy.setTenancyStartDate(orginalLease.getTenancyStartDate());
        leaseCopy.setEndDate(orginalLease.getEndDate());
        leaseCopy.setTenancyEndDate(orginalLease.getTenancyEndDate());
        leaseCopy.setApplicationTenancyPath(orginalLease.getApplicationTenancyPath());
        leaseCopy.setLeaseType(orginalLease.getLeaseType());
        leaseCopy.setType(orginalLease.getType());
        serviceRegistry2.injectServicesInto(leaseCopy);
        final Party tenant = orginalLease.getSecondaryParty();
        if (tenant != null) {
            final AgreementRoleType artLandlord = agreementRoleTypeRepository.find(LeaseAgreementRoleTypeEnum.TENANT);
            leaseCopy.newRole(artLandlord, tenant, null, null);
        }
        final Party landlord = orginalLease.getPrimaryParty();
        if (landlord != null) {
            final AgreementRoleType artLandlord = agreementRoleTypeRepository.find(LeaseAgreementRoleTypeEnum.LANDLORD);
            leaseCopy.newRole(artLandlord, landlord, null, null);
        }
        for (LeaseItem item : orginalLease.getItems()){
            if (referenceDate==null || item.getEffectiveInterval().contains(referenceDate) || (item.getStartDate()!=null && item.getStartDate().isAfter(referenceDate))) {
                LeaseItem newItem = leaseCopy.newItem(
                        item.getType(),
                        item.getInvoicedBy(),
                        item.getCharge(),
                        item.getInvoicingFrequency(),
                        item.getPaymentMethod(),
                        item.getStartDate()
                );
                item.copyTerms(referenceDate, newItem);
            }
        }
        return leaseCopy;
    }

    public void closeOriginalAndOpenNewLeaseItem(final LocalDate startDateNewItem, final LeaseItem originalItem, final InvoicingFrequency invoicingFrequency){
        final Lease lease = originalItem.getLease();
        final LeaseTerm currentTerm = originalItem.currentTerm(startDateNewItem);
        if (currentTerm == null){
            LOG.info(String.format("No current rent term found for lease %s", lease.getReference()));
            return;
        }
        final LeaseItem newItem = lease
                .newItem(originalItem.getType(), originalItem.getInvoicedBy(), originalItem.getCharge(), invoicingFrequency,
                        originalItem.getPaymentMethod(), startDateNewItem);
        newItem.setEndDate(originalItem.getEndDate());
        if (originalItem.getTax()!=null) newItem.setTax(originalItem.getTax());

        // NOTE: the order matters! We take endate of original
        originalItem.changeDates(originalItem.getStartDate(), startDateNewItem.minusDays(1));

        final LeaseTerm newTerm = newItem.newTerm(startDateNewItem, null);
        currentTerm.copyValuesTo(newTerm);
        // link new item to items that had old item as source
        final List<LeaseItemSource> sourceItems = leaseItemSourceRepository.findBySourceItem(originalItem);
        sourceItems.stream()
                .map(lis->lis.getItem()).forEach(li->li.newSourceItem(newItem));
        newItem.verifyUntil(startDateNewItem.plusMonths(2)); // TODO: this looks very random .... maybe derive from amendment item?
    }

    @Inject MessageService messageService;

    @Inject FactoryService factoryService;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject ChargeRepository chargeRepository;

    @Inject LeaseItemSourceRepository leaseItemSourceRepository;

}
