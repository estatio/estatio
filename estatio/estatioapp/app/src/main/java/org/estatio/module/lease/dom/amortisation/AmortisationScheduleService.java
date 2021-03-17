package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
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

import org.estatio.module.base.dom.distribution.Distributable;
import org.estatio.module.base.dom.distribution.DistributionService;
import org.estatio.module.lease.dom.Frequency;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AmortisationScheduleService {

    public static Logger LOG = LoggerFactory.getLogger(AmortisationScheduleService.class);

    @Programmatic
    public void createAndDistributeEntries(final AmortisationSchedule schedule){

        // safeguards
        if (!schedule.getEntries().isEmpty()) return;
        if (schedule.getScheduledValue().compareTo(BigDecimal.ZERO)<1) return;
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
        distributionService.distribute(Lists.newArrayList(schedule.getEntries()), schedule.getScheduledValue(), 2);

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

    public void redistributeEntries(final AmortisationSchedule schedule) {
        final List<Distributable> unreportedEntries = Lists.newArrayList(schedule.getEntries()).stream()
                .filter(e -> e.getDateReported() == null)
                .collect(Collectors.toList());
        final List<AmortisationEntry> reportedEntries = Lists.newArrayList(schedule.getEntries()).stream()
                .filter(e -> e.getDateReported() != null)
                .collect(Collectors.toList());
        final BigDecimal reportedAmount = reportedEntries.stream().map(AmortisationEntry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal unreportedAmount = schedule.getScheduledValue().subtract(reportedAmount);
        if (unreportedAmount.compareTo(BigDecimal.ZERO)<1) return; //Safe guard: in this case everything and more is reported (amortised) already
        distributionService.distribute(unreportedEntries, unreportedAmount, 2);
    }

    @Inject AmortisationScheduleLeaseItemLinkRepository amortisationScheduleLeaseItemLinkRepository;

    @Inject AmortisationScheduleAmendmentItemLinkRepository amortisationScheduleAmendmentItemLinkRepository;

    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

    @Inject AmortisationEntryRepository amortisationEntryRepository;

    @Inject DistributionService distributionService;
}
