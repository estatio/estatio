package org.estatio.module.turnoveraggregate.contributions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Publishing;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@Mixin
public class Lease_aggregateTurnovers {

    public static Logger LOG = LoggerFactory.getLogger(Lease_aggregateTurnovers.class);

    private final Lease lease;

    public Lease_aggregateTurnovers(Lease lease) {
        this.lease = lease;
    }

    @Action(publishing = Publishing.DISABLED)
    public Lease $$(@Nullable final LocalDate startDate, @Nullable final LocalDate endDate, final boolean maintainOnly) {
        LOG.info("Aggregating: " + lease.getReference());
        turnoverAggregationService.aggregateTurnoversForLease(lease, startDate, endDate,
                maintainOnly);
        return lease;
    }

    public String validate$$(final LocalDate startDate, final LocalDate endDate, final boolean maintainOnly){
        if (startDate!=null && endDate!=null){
            if (endDate.isBefore(startDate)) return "The end date cannot be before the start date";
        }
        return null;
    }

    public boolean hide$$(){
        return lease.getStatus()== LeaseStatus.PREVIEW;
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

}
