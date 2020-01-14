package org.estatio.module.turnoveraggregate.contributions;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;

@Mixin
public class TurnoverReportingConfig_aggregations {

    private final TurnoverReportingConfig config;

    public TurnoverReportingConfig_aggregations(TurnoverReportingConfig config) {
        this.config = config;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<TurnoverAggregation> $$() {
        return turnoverAggregationRepository.findByTurnoverReportingConfig(config).stream()
                .sorted(Comparator.comparing(a->a.getDate(), Comparator.reverseOrder()))
                .filter(a->!a.getDate().isAfter(clockService.now()))
                .collect(Collectors.toList());
    }

    @Inject TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject ClockService clockService;

}
