package org.estatio.module.turnoveraggregate.dom;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.module.lease.dom.occupancy.Occupancy;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AggregationReportForOccupancy {

    public AggregationReportForOccupancy(final Occupancy occupancy){
        this.occupancy = occupancy;
        this.aggregationDates = new ArrayList<>();
        this.parallelOccupancies = new ArrayList<>();
        this.parallelOnSameUnit = new ArrayList<>();
    }

    private Occupancy occupancy;

    private List<LocalDate> aggregationDates;

    private List<Occupancy> parallelOnSameUnit;

    private List<Occupancy> parallelOccupancies;

    @Setter
    private Occupancy previousOnSameUnit;

    @Setter
    private Occupancy nextOnSameUnit;

}
