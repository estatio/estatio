package org.estatio.module.turnoveraggregate.dom;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AggregationReportForConfig {

    public AggregationReportForConfig(final TurnoverReportingConfig turnoverReportingConfig){
        this.turnoverReportingConfig = turnoverReportingConfig;
        this.aggregationDates = new ArrayList<>();
        this.parallelOccupancies = new ArrayList<>();
        this.parallelOnSameUnit = new ArrayList<>();
        this.toplevel = false;
    }

    private TurnoverReportingConfig turnoverReportingConfig;

    private List<LocalDate> aggregationDates;

    private List<TurnoverReportingConfig> parallelOnSameUnit;

    private List<TurnoverReportingConfig> parallelOccupancies;

    @Setter
    private boolean toplevel;

    @Setter
    private TurnoverReportingConfig previousOnSameUnit;

    @Setter
    private TurnoverReportingConfig nextOnSameUnit;

}
