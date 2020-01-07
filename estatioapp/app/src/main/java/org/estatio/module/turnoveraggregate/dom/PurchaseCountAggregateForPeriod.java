package org.estatio.module.turnoveraggregate.dom;

import java.math.BigInteger;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnoveraggregate.dom.PurchaseCountAggregateForPeriod"
)
public class PurchaseCountAggregateForPeriod {

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
    @Column(allowsNull = "true")
    private boolean comparable;

    @Programmatic
    public void aggregate(final TurnoverAggregation aggregation){
        turnoverAggregationService.aggregateForPurchaseCount(this, aggregation.getOccupancy(), aggregation.getDate(), aggregation.getType(), aggregation.getFrequency());
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

}
