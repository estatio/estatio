package org.estatio.module.turnoveraggregate.dom;

import java.math.BigInteger;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

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
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnoveraggregate.dom.PurchaseCountAggregateForPeriod "
                        + "WHERE aggregation == :aggregation "
                        + "&& aggregationPeriod == :period "),
})
@Unique(name = "PurchaseCountAggregateForPeriod_aggregation_period_UNQ", members = { "aggregation", "aggregationPeriod" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnoveraggregate.dom.PurchaseCountAggregateForPeriod"
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
    @Column(allowsNull = "true")
    private boolean comparable;

}
