package org.estatio.module.turnoveraggregate.contributions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@Mixin
public class Lease_aggregateTurnovers {

    private final Lease lease;

    public Lease_aggregateTurnovers(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$(@Nullable final LocalDate startDate, @Nullable final LocalDate endDate, final boolean maintainOnly) {
        turnoverAggregationService.aggregateTurnoversForLease(lease, Type.PRELIMINARY, Frequency.MONTHLY, startDate, endDate,
                maintainOnly);
        return lease;
    }

    public String validate$$(final LocalDate startDate, final LocalDate endDate, final boolean maintainOnly){
        if (startDate!=null && endDate!=null){
            if (endDate.isBefore(startDate)) return "The end date cannot be before the start date";
        }
        return null;
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

}
