package org.estatio.module.turnover.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLink;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLinkRepository;

@Mixin
public class TurnoverReportingConfig_removeConfigToAggregateFor {

    private final TurnoverReportingConfig config;

    public TurnoverReportingConfig_removeConfigToAggregateFor(TurnoverReportingConfig config) {
        this.config = config;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public TurnoverReportingConfig $$(final TurnoverReportingConfig childConfig) {
        final TurnoverReportingConfigLink linkToRemove = reportingConfigLinkRepository
                .findByTurnoverReportingConfig(config).stream()
                .filter(l -> l.getAggregationChild().equals(childConfig))
                .findFirst().orElse(null);
        if (linkToRemove!=null){
            reportingConfigLinkRepository.remove(linkToRemove);
        }
        return config;
    }

    public List<TurnoverReportingConfig> choices0$$(final TurnoverReportingConfig childConfig){
        return reportingConfigLinkRepository.findByTurnoverReportingConfig(config).stream().map(l->l.getAggregationChild()).collect(
                Collectors.toList());
    }

    @Inject TurnoverReportingConfigLinkRepository reportingConfigLinkRepository;

}
