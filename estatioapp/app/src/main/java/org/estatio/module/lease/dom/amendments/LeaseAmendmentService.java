package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.estatio.module.application.app.Lease_closeOldAndOpenNewLeaseItem;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.lease.dom.amendments.LeaseAmendmentService"
)
public class LeaseAmendmentService {

    public static Logger LOG = LoggerFactory.getLogger(LeaseAmendmentService.class);

    public void apply(final LeaseAmendment leaseAmendment, final boolean preview) {

        // Only implementation for the moment
        if (leaseAmendment.getLeaseAmendmentType()!=LeaseAmendmentType.COVID_FRA) {
            messageService.warnUser(String.format("Amendment type %s is not implemented (yet...)", leaseAmendment.getLeaseAmendmentType()));
            return;
        }

        final Lease lease = preview ? leaseAmendment.getLeasePreview() : leaseAmendment.getLease();
        final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount = Lists
                .newArrayList(leaseAmendment.getItems()).stream()
                .filter(lai -> lai.getClass().isAssignableFrom(LeaseAmendmentItemForDiscount.class))
                .map(LeaseAmendmentItemForDiscount.class::cast)
                .findFirst().orElse(null);
        if (leaseAmendmentItemForDiscount!=null){
            createDiscountItemsAndTerms(lease, leaseAmendmentItemForDiscount);
        }
        final LeaseAmendmentItemForFrequencyChange leaseAmendmentItemForFrequencyChange = Lists
                .newArrayList(leaseAmendment.getItems()).stream()
                .filter(lai -> lai.getClass().isAssignableFrom(LeaseAmendmentItemForFrequencyChange.class))
                .map(LeaseAmendmentItemForFrequencyChange.class::cast)
                .findFirst().orElse(null);
        if (leaseAmendmentItemForFrequencyChange!=null){
            applyFrequencyChange(lease, leaseAmendmentItemForFrequencyChange);
        }
        leaseAmendment.setState(LeaseAmendmentState.APPLIED);
    }

    void createDiscountItemsAndTerms(
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
            LocalDate startDate = li.getStartDate()==null || li.getStartDate().isBefore(leaseAmendmentItemForDiscount.getStartDate()) ? leaseAmendmentItemForDiscount.getStartDate() : li.getStartDate();
            LocalDate endDate = li.getEndDate()==null || li.getEndDate().isAfter(leaseAmendmentItemForDiscount.getEndDate()) ? leaseAmendmentItemForDiscount.getEndDate() : li.getEndDate();
            // currently we support RENT and SERVICE_CHARGES only
            LeaseItemType newType;
            switch (li.getType()){
            case RENT:
                newType = LeaseItemType.RENT_DISCOUNT_FIXED;
                break;
            case SERVICE_CHARGE:
                newType = LeaseItemType.SERVICE_CHARGE_DISCOUNT_FIXED;
                break;
            default:
                final String warning = String.format("Discount for lease %s and type %s is not implemented (yet)", lease.getReference(), li.getType());
                messageService.warnUser(warning);
                LOG.error(warning);
                newType = null;
            }
            if (newType!=null) {
                // TODO: invoicing frequenct FIXED_IN_ARREARS check with users ...
                // TODO: charge, invoicedBy, payment method from lease item related to discount ... check with users
                // TODO: do we need a new item type related to the source item? .. check with users (this means a discount item for every item discount is related to
                final LeaseItem item = lease
                        .newItem(newType, li.getInvoicedBy(), li.getCharge(),
                                InvoicingFrequency.FIXED_IN_ARREARS, li.getPaymentMethod(), startDate);
                item.setEndDate(endDate);
                final LeaseTermForFixed leaseTerm = (LeaseTermForFixed) item.newTerm(item.getStartDate(), item.getEndDate());
                // TODO: naive implementation not taking indexation into account
                // TODO: should apply a verify first
                final BigDecimal valueForDate = li.valueForDate(startDate);
                if (valueForDate.abs().compareTo(BigDecimal.ZERO)>0){
                    final BigDecimal discountTermValue = valueForDate
                            .multiply(leaseAmendmentItemForDiscount.getDiscountPercentage())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                            .negate();
                    leaseTerm.setValue(discountTermValue);
                }

            }
        }
    }

    void applyFrequencyChange(final Lease lease,
            final LeaseAmendmentItemForFrequencyChange leaseAmendmentItemForFrequencyChange){
        for (LeaseItemType leaseItemType : LeaseAmendmentItem.applicableToFromString(leaseAmendmentItemForFrequencyChange.getApplicableTo())){
            switch (leaseItemType){
            case RENT:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.RENT, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
                break;
            case SERVICE_CHARGE:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.SERVICE_CHARGE, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
                break;
            case SERVICE_CHARGE_INDEXABLE:
                factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, lease).act(leaseAmendmentItemForFrequencyChange.getStartDate(), LeaseItemType.SERVICE_CHARGE, leaseAmendmentItemForFrequencyChange.getInvoicingFrequencyOnLease(), leaseAmendmentItemForFrequencyChange.getAmendedInvoicingFrequency(), false);
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

    @Inject MessageService messageService;

    @Inject FactoryService factoryService;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject AgreementRoleTypeRepository agreementRoleTypeRepository;

}
