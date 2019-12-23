package org.estatio.module.turnover.dom.aggregate;

import java.math.BigInteger;

import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.aggregate.PurchaseCountAggregateForPeriod"
)
public class PurchaseCountAggregateForPeriod {

    @Getter @Setter
    @Column(name = "turnoverAggregationId", allowsNull = "false")
    private TurnoverAggregation aggregation;

    @Getter @Setter
    @Column(allowsNull = "false")
    private AggregationPeriod aggregationPeriod;

     /*
      * TurnoverPurchaseCount1MCY
      * TurnoverPurchaseCount1MPY
      * IsComparableTPC1M
      */

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger count;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger countPreviousYear;

    @Getter @Setter
    private boolean comparable;

}
