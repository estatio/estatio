package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;

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
        objectType = "org.estatio.module.turnoveraggregate.dom.TurnoverAggregateToDate"
)
public class TurnoverAggregateToDate {

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean nonComparableThisYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private int turnoverCount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean nonComparablePreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private int turnoverCountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean comparable;

    @Programmatic
    public void aggregate(final TurnoverAggregation aggregation){
        turnoverAggregationService.aggregateToDate(this, aggregation.getOccupancy(), aggregation.getDate(), aggregation.getType(), aggregation.getFrequency());
    }

    @Inject
    TurnoverAggregationService turnoverAggregationService;

}
