package org.estatio.module.turnover.contributions;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;

@Mixin
public class Occupancy_createEmptyTurnovers {



    private final Occupancy occupancy;

    public Occupancy_createEmptyTurnovers(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action()
    public Occupancy $$(final LocalDate startDate, final LocalDate endDate) {
        if (!endDate.isBefore(startDate)){
            final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository.findByOccupancy(occupancy);
            LocalDate date = startDate;
            while (!date.isAfter(endDate)){
                for (TurnoverReportingConfig config : configs){
                    config.produceEmptyTurnover(date);
                }
                date = date.plusDays(1);
            }
        }
        return occupancy;
    }

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;


}
