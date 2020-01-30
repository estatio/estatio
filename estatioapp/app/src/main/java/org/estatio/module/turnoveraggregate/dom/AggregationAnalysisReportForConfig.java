package org.estatio.module.turnoveraggregate.dom;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.aggregation.AggregationPattern;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AggregationAnalysisReportForConfig {

    public AggregationAnalysisReportForConfig(final TurnoverReportingConfig turnoverReportingConfig){
        this.turnoverReportingConfig = turnoverReportingConfig;
        this.aggregationDates = new ArrayList<>();
        this.parallelConfigs = new ArrayList<>();
        this.parallelOnSameUnit = new ArrayList<>();
        this.previousOnOtherUnit = new ArrayList<>();
        this.nextOnOtherUnit = new ArrayList<>();
        this.configsToIncludeInAggregation = new ArrayList<>();
        this.toplevel = false;
    }

    private TurnoverReportingConfig turnoverReportingConfig;

    private List<LocalDate> aggregationDates;

    private List<TurnoverReportingConfig> parallelOnSameUnit;

    private List<TurnoverReportingConfig> parallelConfigs;

    @Setter
    private boolean toplevel;

    @Setter
    private TurnoverReportingConfig previousOnSameUnit;

    private List<TurnoverReportingConfig> previousOnOtherUnit;

    @Setter
    private TurnoverReportingConfig nextOnSameUnit;

    private List<TurnoverReportingConfig> nextOnOtherUnit;

    @Setter
    private Lease previousLease;

    @Setter
    private Lease nextLease;

    @Setter
    private AggregationPattern aggregationPattern;

    private List<TurnoverReportingConfig> configsToIncludeInAggregation;
}
