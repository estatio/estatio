package org.estatio.module.application.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSource;
import org.estatio.module.lease.dom.LeaseItemSourceRepository;
import org.estatio.module.lease.dom.LeaseItemStatus;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

@Mixin(method = "act")
public class Lease_closeOldAndOpenNewLeaseItem {

    private static final Logger LOG = LoggerFactory.getLogger(Lease_closeOldAndOpenNewLeaseItem.class);

    private final Lease lease;

    public Lease_closeOldAndOpenNewLeaseItem(Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, hidden = Where.EVERYWHERE)
    public Lease act(
            final LocalDate startDateNewItem,
            final LeaseItemType type,
            final InvoicingFrequency oldInvoicingFrequency,
            final InvoicingFrequency newInvoicingFrequency,
            final boolean removeInvoicesOldItem
            ) {
        if (validateAct(startDateNewItem, type, oldInvoicingFrequency, newInvoicingFrequency, removeInvoicesOldItem)==null) {

            final List<LeaseItem> activeItemsOfType = lease.findItemsOfType(type)
                    .stream()
                    .filter(i->i.getInvoicingFrequency()!=null)
                    .filter(i->i.getInvoicingFrequency().equals(oldInvoicingFrequency))
                    .filter(i->i.getEffectiveInterval()!=null)
                    .filter(i -> i.getEffectiveInterval().contains(startDateNewItem))
                    .collect(Collectors.toList());
            for (LeaseItem item : activeItemsOfType) {
                if (item.getInvoicedBy() == LeaseAgreementRoleTypeEnum.LANDLORD && item.getStatus()!= LeaseItemStatus.SUSPENDED && item.getStatus()!= LeaseItemStatus.TERMINATED) {

                    item.verifyUntil(startDateNewItem);

                    switch (type) {

                    case RENT:
                        LOG.info(
                                String.format(
                                        "Creating new rent item for %s with startdate %s and frequency %s while ending item with frequency %s",
                                        lease.getReference(),
                                        startDateNewItem.toString(),
                                        newInvoicingFrequency.title(),
                                        item.getInvoicingFrequency().title()
                                )
                        );
                        closeAndOpenNewRentItem(startDateNewItem, item, newInvoicingFrequency, removeInvoicesOldItem);
                        break;

                    case SERVICE_CHARGE:
                        LOG.info(
                                String.format(
                                        "Creating new service charge item for %s with startdate %s and frequency %s while ending item with frequency %s",
                                        lease.getReference(),
                                        startDateNewItem.toString(),
                                        newInvoicingFrequency.title(),
                                        item.getInvoicingFrequency().title()
                                )
                        );
                        closeAndOpenNewServiceChargeItem(startDateNewItem, item, newInvoicingFrequency,
                                removeInvoicesOldItem);
                        break;

                    case SERVICE_CHARGE_INDEXABLE:
                        LOG.info(
                                String.format(
                                        "Creating new service charge indexable item for %s with startdate %s and frequency %s while ending item with frequency %s",
                                        lease.getReference(),
                                        startDateNewItem.toString(),
                                        newInvoicingFrequency.title(),
                                        item.getInvoicingFrequency().title()
                                )
                        );
                        closeAndOpenNewServiceChargeIndexableItem(startDateNewItem, item, newInvoicingFrequency,
                                removeInvoicesOldItem);
                        break;

                    default:
                        // NOT SUPPORTED YET
                    }

                }

            }

        }

        return lease;
    }

    public String validateAct(final LocalDate startDateNewItem,
            final LeaseItemType type,
            final InvoicingFrequency oldInvoicingFrequency,
            final InvoicingFrequency invoicingFrequency,
            final boolean removeInvoicesOldItem){
        if (!startDateNewItem.equals(new LocalDate(2020,4,1))) return "Currently only start date 2020-4-1 supported";
        if (!Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE, LeaseItemType.SERVICE_CHARGE_INDEXABLE).contains(type)) return "Currently only rent and service charges supported";
        if (oldInvoicingFrequency!=InvoicingFrequency.QUARTERLY_IN_ADVANCE) return "Currently only old frequency Quarterly In Advance supported";
        if (invoicingFrequency!=InvoicingFrequency.MONTHLY_IN_ADVANCE) return "Currently only new frequency Monthly In Advance supported";
        return null;
    }


    public void closeAndOpenNewRentItem(final LocalDate startDateNewItem, final LeaseItem item, final InvoicingFrequency invoicingFrequency, final boolean removeInvoicesOldItem){
        final LeaseTerm currentTerm = item.currentTerm(startDateNewItem);
        if (currentTerm == null){
            LOG.info(String.format("No current rent term found for lease %s", lease.getReference()));
            return;
        }
        LeaseTermForIndexable currentTermForIndexable = (LeaseTermForIndexable) currentTerm;
        item.changeDates(item.getStartDate(), startDateNewItem.minusDays(1));
        final LeaseItem newRentItem = lease
                .newItem(LeaseItemType.RENT, item.getInvoicedBy(), item.getCharge(), invoicingFrequency,
                        item.getPaymentMethod(), startDateNewItem);
        if (item.getTax()!=null) newRentItem.setTax(item.getTax());
        final LeaseTermForIndexable newTermForIndexable = (LeaseTermForIndexable) newRentItem.newTerm(startDateNewItem, null);
        currentTermForIndexable.copyValuesTo(newTermForIndexable);
        // link new item to items that had old item as source
        final List<LeaseItemSource> sourceItems = leaseItemSourceRepository.findBySourceItem(item);
        sourceItems.stream()
        .map(lis->lis.getItem()).forEach(li->li.newSourceItem(newRentItem));

        if (removeInvoicesOldItem) {
            removeInvoicesStartingWith(currentTerm, startDateNewItem);
        }
        newRentItem.verifyUntil(startDateNewItem.plusMonths(2));
    }

    public void closeAndOpenNewServiceChargeIndexableItem(final LocalDate startDateNewItem, final LeaseItem item, final InvoicingFrequency invoicingFrequency, final boolean removeInvoicesOldItem){
        final LeaseTerm currentTerm = item.currentTerm(startDateNewItem);
        if (currentTerm == null){
            LOG.info(String.format("No current service charge indexable term found for lease %s", lease.getReference()));
            return;
        }
        LeaseTermForIndexable currentSCITerm = (LeaseTermForIndexable) currentTerm;
        item.changeDates(item.getStartDate(), startDateNewItem.minusDays(1));
        final LeaseItem newServiceChargeIndexableItem = lease
                .newItem(LeaseItemType.SERVICE_CHARGE_INDEXABLE, item.getInvoicedBy(), item.getCharge(), invoicingFrequency,
                        item.getPaymentMethod(), startDateNewItem);
        if (item.getTax()!=null) newServiceChargeIndexableItem.setTax(item.getTax());
        final LeaseTermForIndexable newSCITerm = (LeaseTermForIndexable) newServiceChargeIndexableItem.newTerm(startDateNewItem, null);
        currentSCITerm.copyValuesTo(newSCITerm);
        if (removeInvoicesOldItem) {
            removeInvoicesStartingWith(currentTerm, startDateNewItem);
        }
        newServiceChargeIndexableItem.verifyUntil(startDateNewItem.plusMonths(2));
    }

    public void closeAndOpenNewServiceChargeItem(final LocalDate startDateNewItem, final LeaseItem item, final InvoicingFrequency invoicingFrequency, final boolean removeInvoicesOldItem){
        final LeaseTerm currentTerm = item.currentTerm(startDateNewItem);
        if (currentTerm == null){
            LOG.info(String.format("No current service charge term found for lease %s", lease.getReference()));
            return;
        }
        LeaseTermForServiceCharge currentSCTerm = (LeaseTermForServiceCharge) currentTerm;
        item.changeDates(item.getStartDate(), startDateNewItem.minusDays(1));
        final LeaseItem newServiceChargeItem = lease
                .newItem(LeaseItemType.SERVICE_CHARGE, item.getInvoicedBy(), item.getCharge(), invoicingFrequency,
                        item.getPaymentMethod(), startDateNewItem);
        if (item.getTax()!=null) newServiceChargeItem.setTax(item.getTax());
        final LeaseTermForServiceCharge newSCTerm = (LeaseTermForServiceCharge) newServiceChargeItem.newTerm(startDateNewItem, null);
        currentSCTerm.copyValuesTo(newSCTerm);
        if (removeInvoicesOldItem) {
            removeInvoicesStartingWith(currentTerm, startDateNewItem);
        }
        newServiceChargeItem.verifyUntil(startDateNewItem.plusMonths(2));
    }

    public void removeInvoicesStartingWith(final LeaseTerm term, final LocalDate startDateNewItem){
        LeaseTerm t = term;
        List<Invoice> invoicesToDelete = new ArrayList<>();
        while (t!=null){
            final List<Invoice> invoicesForTerm = t.getInvoiceItems().stream()
                    .map(ii -> ii.getInvoice())
                    .filter(i->i.getStatus()==InvoiceStatus.APPROVED || i.getStatus()==InvoiceStatus.NEW)
                    .filter(i -> !i.getDueDate().isBefore(startDateNewItem))
                    .distinct()
                    .collect(Collectors.toList());
            invoicesToDelete.addAll(invoicesForTerm);
            t = t.getNext();
        }
        invoicesToDelete.stream().distinct()
        .forEach(i -> {
            final String number = i.getInvoiceNumber()==null ?  i.getCollectionNumber()==null ? "no number" : i.getCollectionNumber() : i.getInvoiceNumber();
            LOG.info(String.format("Deleting invoice %s for lease %s", number, lease.getReference()));
            repositoryService.removeAndFlush(i);
        });
    }

    @Inject
    RepositoryService repositoryService;

    @Inject LeaseItemSourceRepository leaseItemSourceRepository;

}
