package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.distribution.DistributionService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AmortisationScheduleService {

    public static Logger LOG = LoggerFactory.getLogger(AmortisationScheduleService.class);

    public LocalDate firstInvoiceDateForLeaseItem(final LeaseItem leaseItem){
        List<LocalDate> invoiceDates = new ArrayList<>();
        for (LeaseTerm leaseTerm : leaseItem.getTerms()){
            final List<LocalDate> datesForTerm = Lists.newArrayList(leaseTerm.getInvoiceItems()).stream()
                    .filter(ii -> ii.getInvoice().getStatus() == InvoiceStatus.INVOICED)
                    .map(ii -> ii.getInvoice().getInvoiceDate())
                    .collect(Collectors.toList());
            if (!datesForTerm.isEmpty()) invoiceDates.addAll(datesForTerm);
        }
        return invoiceDates.stream().min(LocalDate::compareTo).orElse(null);
    }

    @Programmatic
    public AmortisationSchedule findOrCreateAmortisationScheduleForLeaseAndCharge(
            final Lease lease,
            final Charge charge,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){

        if (!LocalDateInterval.including(startDate, endDate).isValid()){
            String msg = String.format("Cannot create schedule for lease %s and charge %s : startDate %s and endDate %s are not a valid interval" , lease.getReference(), charge.getReference(), startDate, endDate);
            LOG.warn(msg);
            return null;
        }

        if (amortisationScheduleRepository.findUnique(lease, charge, startDate)!=null) {
            return amortisationScheduleRepository.findUnique(lease, charge, startDate);
        }

        final List<LeaseItem> itemsToCalculateAndLink = Lists.newArrayList(lease.getItems()).stream()
                .filter(li -> li.getCharge().equals(charge))
                .filter(li -> amortisationScheduleLeaseItemLinkRepository.findByLeaseItem(li).isEmpty()) // we filter items that are linked to an amortisation schedule already
                .collect(Collectors.toList());

        if (itemsToCalculateAndLink.isEmpty()){
            String msg = String.format("No lease items could be found for a schedule for lease %s, charge %s and startDate %s", lease.getReference(), charge.getReference(), startDate);
            LOG.warn(msg);
            return null;
        }

        List<InvoiceCalculationService.CalculationResult> calculationResults = new ArrayList<>();
        for (LeaseItem item : itemsToCalculateAndLink){
            for (LeaseTerm term : item.getTerms()){
                calculationResults.addAll(term.calculationResults(item.getEffectiveInterval()));
            }
        }

        final BigDecimal scheduledAmount = calculationResults.stream()
                .map(r -> r.value())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .negate();

        if (scheduledAmount.compareTo(BigDecimal.ZERO)<1){
            String msg = String.format("The scheduled amount %s should be larger than 0. Could not create a schedule for lease %s, charge %s and startDate %s", scheduledAmount, lease.getReference(), charge.getReference(), startDate);
            LOG.warn(msg);
            return null;
        }

        final AmortisationSchedule amortisationSchedule = amortisationScheduleRepository
                .findOrCreate(lease, charge, scheduledAmount, frequency, startDate, endDate);

        for (LeaseItem item : itemsToCalculateAndLink){
            amortisationScheduleLeaseItemLinkRepository.findOrCreate(amortisationSchedule, item);
            if (item.getLeaseAmendmentItem()!=null && item.getLeaseAmendmentItem().getClass().isAssignableFrom(LeaseAmendmentItemForDiscount.class)){
                amortisationScheduleAmendmentItemLinkRepository.findOrCreate(amortisationSchedule,
                        (LeaseAmendmentItemForDiscount) item.getLeaseAmendmentItem());
            }
        }

        final List<AmortisationScheduleAmendmentItemLink> linksToAmendmentItems = amortisationScheduleAmendmentItemLinkRepository
                .findBySchedule(amortisationSchedule);
        if (!linksToAmendmentItems.isEmpty()) {
            final BigDecimal amountDerivedFromLinkedAmendmentItems = linksToAmendmentItems
                    .stream()
                    .map(l -> l.getLeaseAmendmentItemForDiscount().getCalculatedDiscountAmount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (amountDerivedFromLinkedAmendmentItems.compareTo(scheduledAmount) != 0) {
                String note = String.format("The amount derived from the linked amendment item(s) %s does not match the scheduled amount derived from the linked lease item(s)!", amountDerivedFromLinkedAmendmentItems);
                amortisationSchedule.setNote(note);
            }
        }

        amortisationSchedule.createAndDistributeEntries();

        return amortisationSchedule;

    }

    @Programmatic
    public void createAndDistributeEntries(final AmortisationSchedule schedule){

        // safeguards
        if (!schedule.getEntries().isEmpty()) return;
        if (schedule.getScheduledAmount().compareTo(BigDecimal.ZERO)<1) return;
        if (!Arrays.asList(Frequency.MONTHLY, Frequency.QUARTERLY).contains(schedule.getFrequency())) return;

        // first entry creation
        final LocalDate startDate = schedule.getStartDate();
        amortisationEntryRepository.findOrCreate(schedule, startDate, BigDecimal.ZERO);

        // subsequent entries
        // init
        LocalDate date = nextDate(startDate, schedule.getFrequency());
        // while
        while (!date.isAfter(schedule.getEndDate())){
            amortisationEntryRepository.findOrCreate(schedule, date, BigDecimal.ZERO);
            date = nextDate(date, schedule.getFrequency());
        }

        // distribution of amount
        distributionService.distribute(Lists.newArrayList(schedule.getEntries()), schedule.getScheduledAmount(), 2);

    }

    LocalDate nextDate(final LocalDate date, final Frequency frequency){

        LocalDate result;
        switch (frequency){
        case QUARTERLY:

            switch (date.getMonthOfYear()){
            case 1:
            case 2:
            case 3:
                result = frequency.nextDate(date.withDayOfMonth(1).withMonthOfYear(1));
                break;

            case 4:
            case 5:
            case 6:
                result = frequency.nextDate(date.withDayOfMonth(1).withMonthOfYear(4));
                break;

            case 7:
            case 8:
            case 9:
                result = frequency.nextDate(date.withDayOfMonth(1).withMonthOfYear(7));
                break;

            default:
                result = frequency.nextDate(date.withDayOfMonth(1).withMonthOfYear(10));
            }
            break;

        case MONTHLY:
            result = frequency.nextDate(date.withDayOfMonth(1));
            break;

        default:
            result = null;
            break;
        }

        return result;

    }

    @Inject AmortisationScheduleLeaseItemLinkRepository amortisationScheduleLeaseItemLinkRepository;

    @Inject AmortisationScheduleAmendmentItemLinkRepository amortisationScheduleAmendmentItemLinkRepository;

    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

    @Inject AmortisationEntryRepository amortisationEntryRepository;

    @Inject DistributionService distributionService;

}
