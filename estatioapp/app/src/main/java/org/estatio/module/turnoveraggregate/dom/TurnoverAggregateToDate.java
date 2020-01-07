package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Type;

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
    public void aggregate(final Occupancy occupancy, final LocalDate aggregationDate, final Type type, final Frequency frequency){
        turnoverAggregationService.aggregateToDate(this, occupancy, aggregationDate, type, frequency);
    }

    @Inject
    TurnoverAggregationService turnoverAggregationService;

}
