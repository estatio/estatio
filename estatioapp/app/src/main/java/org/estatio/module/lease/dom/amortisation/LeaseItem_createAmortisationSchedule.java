package org.estatio.module.lease.dom.amortisation;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseStatus;

@Mixin
public class LeaseItem_createAmortisationSchedule {

    private final LeaseItem leaseItem;

    public LeaseItem_createAmortisationSchedule(LeaseItem leaseItem) {
        this.leaseItem = leaseItem;
    }

    @Action()
    public LeaseItem $$(
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        amortisationScheduleService.findOrCreateAmortisationScheduleForLeaseItem(leaseItem, frequency, startDate, endDate);
        return leaseItem;
    }

    public List<Frequency> choices0$$(){
        // currently the only frequencies supported
        return Arrays.asList(Frequency.MONTHLY, Frequency.QUARTERLY);
    }

    public String validate$$(
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        if (!LocalDateInterval.including(startDate,endDate).isValid()) return "The start date and the end date should be a valid interval";
        return null;
    }

    public boolean hide$$(){
        return leaseItem.getLease().getStatus()== LeaseStatus.PREVIEW;
    }

    @Inject
    AmortisationScheduleService amortisationScheduleService;

}
