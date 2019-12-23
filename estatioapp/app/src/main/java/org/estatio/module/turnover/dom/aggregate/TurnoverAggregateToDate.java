package org.estatio.module.turnover.dom.aggregate;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
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
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.aggregate.TurnoverAggregateToDate"
)
public class TurnoverAggregateToDate {

    @Getter @Setter
    @Column(name = "turnoverAggregationId", allowsNull = "false")
    private TurnoverAggregation aggregation;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmount;

    @Getter @Setter
    private boolean nonComparableThisYear;

    @Getter @Setter
    private int turnoverCount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmountPreviousYear;

    @Getter @Setter
    private boolean nonComparablePreviousYear;

    @Getter @Setter
    private int turnoverCountPreviousYear;

    @Getter @Setter
    private boolean comparable;

}
