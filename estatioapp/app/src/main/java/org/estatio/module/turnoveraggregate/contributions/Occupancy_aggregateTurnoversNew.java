package org.estatio.module.turnoveraggregate.contributions;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@Mixin
public class Occupancy_aggregateTurnoversNew {



    private final Occupancy occupancy;

    public Occupancy_aggregateTurnoversNew(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action()
    public Occupancy $$(final LocalDate aggregationDate) {
        turnoverAggregationService.aggregateTurnoversForOccupancy(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, aggregationDate);
        return occupancy;
    }

    @Inject
    TurnoverAggregationService turnoverAggregationService;

    @Inject WrapperFactory wrapperFactory;

    @Inject FactoryService factoryService;


}
