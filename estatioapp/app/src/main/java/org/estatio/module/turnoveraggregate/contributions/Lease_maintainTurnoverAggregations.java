package org.estatio.module.turnoveraggregate.contributions;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@Mixin
public class Lease_maintainTurnoverAggregations {

    private final Lease lease;

    public Lease_maintainTurnoverAggregations(Lease lease) {
        this.lease = lease;
    }

    @Action()
    public Lease $$() {
        turnoverAggregationService.maintainTurnoverAggregationsForLease(lease, Type.PRELIMINARY, Frequency.MONTHLY);
        return lease;
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

}
