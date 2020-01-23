package org.estatio.module.turnover.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLinkRepository;

@Mixin
public class TurnoverReportingConfig_configsToAggregateFor {

    private final TurnoverReportingConfig config;

    public TurnoverReportingConfig_configsToAggregateFor(TurnoverReportingConfig config) {
        this.config = config;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<TurnoverReportingConfig> $$() {
        return reportingConfigLinkRepository.findByTurnoverReportingConfig(config).stream()
        .map(l->l.getAggregationChild()).collect(Collectors.toList());
    }

    @Inject TurnoverReportingConfigLinkRepository reportingConfigLinkRepository;

}
