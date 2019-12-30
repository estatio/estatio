package org.estatio.module.turnoveraggregate.dom;

public enum AggregationPeriod {

    P_1M("1M"),
    P_2M("2M"),
    P_3M("3M"),
    P_6M("6M"),
    P_9M("9M"),
    P_12M("12M");

    private final String name;

    AggregationPeriod(final String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    };

}
