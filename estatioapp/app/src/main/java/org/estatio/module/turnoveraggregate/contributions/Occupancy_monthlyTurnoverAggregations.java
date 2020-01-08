package org.estatio.module.turnoveraggregate.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

@Mixin
public class Occupancy_monthlyTurnoverAggregations {
    
    private final Occupancy occupancy;

    public Occupancy_monthlyTurnoverAggregations(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "hide")
    public List<TurnoverAggregation> $$() {
        return turnoverAggregationRepository.findByOccupancyAndTypeAndFrequencyOnOrBeforeDate(occupancy, Type.PRELIMINARY, Frequency.MONTHLY, clockService.now());
    }

   @Inject TurnoverAggregationRepository turnoverAggregationRepository;

   @Inject ClockService clockService;

}
