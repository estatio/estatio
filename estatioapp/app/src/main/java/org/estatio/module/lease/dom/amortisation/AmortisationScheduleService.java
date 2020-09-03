package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.Arrays;

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
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AmortisationScheduleService {

    public static Logger LOG = LoggerFactory.getLogger(AmortisationScheduleService.class);

    @Programmatic
    public void createAmortisationScheduleForLeaseAndCharge(
            final Lease lease,
            final Charge charge,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){

        if (!LocalDateInterval.including(startDate, endDate).isValid()){
            String msg = String.format("Cannot create schedule for lease %s and charge %s : startDate %s and endDate %s are not a valid interval" , lease.getReference(), charge.getReference(), startDate, endDate);
            LOG.warn(msg);
            return;
        }

        if (amortisationScheduleRepository.findUnique(lease, charge, startDate)!=null) {
            String msg = String.format("There is already a schedule for lease %s, charge %s and startDate %s", lease.getReference(), charge.getReference(), startDate);
            LOG.warn(msg);
            return;
        }

        BigDecimal scheduledAmount = BigDecimal.ZERO; // TODO: calculate
        // TODO: in order to make this generic, we have to take all items with the charge on the lease, that are not already linked to another amortisation schedule
        // Then for each we have to calculate the total value that will be invoiced; the sum of which is the scheduled amount.

        final AmortisationSchedule amortisationSchedule = amortisationScheduleRepository
                .findOrCreate(lease, charge, scheduledAmount, frequency, startDate, endDate);

        amortisationSchedule.createAndDistributeEntries();

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

    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

    @Inject AmortisationEntryRepository amortisationEntryRepository;

    @Inject DistributionService distributionService;

}
