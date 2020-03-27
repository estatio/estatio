package org.estatio.module.turnoveraggregate.contributions;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.dom.AggregationViewModel;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

@Mixin()
public class Occupancy_reviewAggregation {

    private final Occupancy occupancy;

    public Occupancy_reviewAggregation(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public AggregationViewModel reviewAggregation(final LocalDate aggregationDate) {
        final TurnoverReportingConfig config = turnoverReportingConfigRepository
                .findUnique(occupancy, Type.PRELIMINARY);
        if (config==null) return null;
        return new AggregationViewModel(
                config, aggregationDate, turnoverAggregationRepository.findUnique(config, aggregationDate));
    }

    public LocalDate default0ReviewAggregation(){
        return clockService.now().withDayOfMonth(1).minusMonths(1);
    }

    public String disableReviewAggregation(){
        if (turnoverReportingConfigRepository
                .findUnique(occupancy, Type.PRELIMINARY)==null) return "No configuration with aggregations found";
        return null;
    }

    @Inject
    ClockService clockService;

    @Inject
    TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;
}
