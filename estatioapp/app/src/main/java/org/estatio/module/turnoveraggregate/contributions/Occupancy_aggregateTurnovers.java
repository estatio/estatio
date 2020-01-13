package org.estatio.module.turnoveraggregate.contributions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

@Mixin
public class Occupancy_aggregateTurnovers {



    private final Occupancy occupancy;

    public Occupancy_aggregateTurnovers(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action()
    public Occupancy $$(final LocalDate calculationDate) {
        turnoverReportingConfigRepository.findByOccupancyAndTypeAndFrequency(occupancy, Type.PRELIMINARY, Frequency.MONTHLY).forEach(
                trc-> {
                    final TurnoverReportingConfig_aggregateTurnovers mixin = factoryService
                            .mixin(TurnoverReportingConfig_aggregateTurnovers.class, trc);
                    wrapperFactory.wrap(mixin).$$(calculationDate);
                }
        );
        return occupancy;
    }

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject WrapperFactory wrapperFactory;

    @Inject FactoryService factoryService;


}
