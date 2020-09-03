package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.base.dom.distribution.DistributionService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AmortisationScheduleService {

    @Programmatic
    public void createAndDistributeEntries(final AmortisationSchedule schedule){

        // safeguards
        if (!schedule.getEntries().isEmpty()) return;
        if (schedule.getScheduledAmount().compareTo(BigDecimal.ZERO)<1) return;

        // first entry creation
        final LocalDate startDate = schedule.getStartDate();
        amortisationEntryRepository.findOrCreate(schedule, startDate, BigDecimal.ZERO);

        // subsequent entries
        // init
        LocalDate date = startDate.withDayOfMonth(1);
        date = schedule.getFrequency().nextDate(date);
        // while
        while (!date.isAfter(schedule.getEndDate())){
            amortisationEntryRepository.findOrCreate(schedule, date, BigDecimal.ZERO);
            date = schedule.getFrequency().nextDate(date);
        }

        // distribution of amount
        distributionService.distribute(Lists.newArrayList(schedule.getEntries()), schedule.getScheduledAmount(), 2);

    }

    @Inject AmortisationEntryRepository amortisationEntryRepository;

    @Inject DistributionService distributionService;

}
