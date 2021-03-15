package org.estatio.module.turnover.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnoveraggregate.dom.TurnoverAggregationService;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLinkRepository;

@Mixin
public class TurnoverReportingConfig_addConfigToAggregateFor {

    private final TurnoverReportingConfig config;

    public TurnoverReportingConfig_addConfigToAggregateFor(TurnoverReportingConfig config) {
        this.config = config;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public TurnoverReportingConfig $$(final TurnoverReportingConfig childConfig) {
        reportingConfigLinkRepository
                .findOrCreate(config, childConfig);
        return config;
    }

    public List<TurnoverReportingConfig> choices0$$(){
        return turnoverAggregationService.choicesForChildConfig(config);
    }

    @Inject TurnoverReportingConfigLinkRepository reportingConfigLinkRepository;

    @Inject TurnoverAggregationService turnoverAggregationService;

}
