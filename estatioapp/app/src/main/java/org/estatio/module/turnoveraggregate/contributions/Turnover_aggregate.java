package org.estatio.module.turnoveraggregate.contributions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.background.BackgroundService2;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@Mixin
public class Turnover_aggregate {

    public static Logger LOG = LoggerFactory.getLogger(Turnover_aggregate.class);

    private final Turnover turnover;

    public Turnover_aggregate(Turnover turnover) {
        this.turnover = turnover;
    }

    @Action()
    public Turnover $$() {
        LOG.info(String.format("Aggregating turnover for date %s and lease %s", turnover.getDate(), turnover.getConfig().getOccupancy().getLease().getReference()));
        turnoverAggregationService.aggregate(turnover);
        return turnover;
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

}
