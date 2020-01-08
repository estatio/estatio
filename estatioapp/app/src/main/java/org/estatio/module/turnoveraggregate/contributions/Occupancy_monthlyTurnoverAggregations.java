package org.estatio.module.turnoveraggregate.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

@Mixin
public class Occupancy_monthlyTurnoverAggregations {
    
    private final Occupancy occupancy;

    public Occupancy_monthlyTurnoverAggregations(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Collection()
    public List<TurnoverAggregation> $$() {
        return turnoverAggregationRepository.findByOccupancyAndTypeAndFrequency(occupancy, Type.PRELIMINARY, Frequency.MONTHLY);
    }

   @Inject TurnoverAggregationRepository turnoverAggregationRepository;

}
