package org.estatio.module.turnoveraggregate.contributions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregation;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationRepository;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;

@Mixin
public class TurnoverReportingConfig_aggregateTurnovers {

    private final TurnoverReportingConfig turnoverReportingConfig;

    public TurnoverReportingConfig_aggregateTurnovers(TurnoverReportingConfig turnoverReportingConfig) {
        this.turnoverReportingConfig = turnoverReportingConfig;
    }

    @Action()
    public TurnoverReportingConfig $$(final LocalDate date) {
        turnoverAggregationService.calculateForConfig(turnoverReportingConfig, date);
        return turnoverReportingConfig;
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

}
