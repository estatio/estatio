package org.estatio.module.turnover.dom;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;

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
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Turnover_lease_date_UNQ", members = {"occupancy", "reportedAt"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE occupancy == :occupancy "
                        + "&& reportedAt == :reportedAt "),
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.Turnover"
)
public class Turnover extends UdoDomainObject2<Turnover> {

    public Turnover(){
        super("occupancy, reportedAt");
    }

    public Turnover(final Occupancy occupancy,
            final LocalDate date,
            final Type type,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal turnoverNetAmount,
            final BigDecimal turnoverGrossAmount,
            final BigInteger turnoverPurchaseCount,
            final String comments,
            final boolean nonComparable){
        this();
        this.occupancy = occupancy;
        this.date = date;
        this.type = type;
        this.reportedAt = reportedAt;
        this.reportedBy = reportedBy;
        this.currency = currency;
        this.turnoverNetAmount = turnoverNetAmount;
        this.turnoverGrossAmount = turnoverGrossAmount;
        this.turnoverPurchaseCount = turnoverPurchaseCount;
        this.comments = comments;
        this.nonComparable = nonComparable;
    }

    @Getter @Setter
    @Column(name = "occupancyId", allowsNull = "false")
    private Occupancy occupancy;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Type type;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDateTime reportedAt;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String reportedBy;

    @Getter @Setter
    @Column(name = "currencyId", allowsNull = "false")
    private Currency currency;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal turnoverNetAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal turnoverGrossAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger turnoverPurchaseCount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String comments;

    @Getter @Setter
    @Column(allowsNull = "false")
    private boolean nonComparable;

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOccupancy().getApplicationTenancy();
    }

}
