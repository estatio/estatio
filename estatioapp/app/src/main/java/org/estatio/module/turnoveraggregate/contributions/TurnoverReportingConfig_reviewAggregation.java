package org.estatio.module.turnoveraggregate.contributions;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnoveraggregate.dom.AggregationViewModel;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

@Mixin()
public class TurnoverReportingConfig_reviewAggregation {

    private final TurnoverReportingConfig config;

    public TurnoverReportingConfig_reviewAggregation(TurnoverReportingConfig config) {
        this.config = config;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public AggregationViewModel $$(final LocalDate aggregationDate) {
        return new AggregationViewModel(config, aggregationDate, turnoverAggregationRepository.findUnique(config, aggregationDate));
    }

    public LocalDate default0$$(){
        return clockService.now().withDayOfMonth(1).minusMonths(1);
    }

    @Inject
    ClockService clockService;

    @Inject
    TurnoverAggregationRepository turnoverAggregationRepository;
}
