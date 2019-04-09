package org.estatio.module.turnover.dom;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.Lease;

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
                name = "Turnover_lease_date_UNQ", members = {"lease", "date"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnover.dom.Turnover "
                        + "WHERE lease == :lease "
                        + "&& date == :date "),
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.Turnover"
)
public class Turnover extends UdoDomainObject2<Turnover> {

    public Turnover(){
        super("lease, date");
    }

    public Turnover(final Lease lease,
            final LocalDate date,
            final BigDecimal amount,
            final Currency currency){
        this();
        this.lease = lease;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
    }

    @Getter @Setter
    @Column(name = "leaseId", allowsNull = "false")
    private Lease lease; // TODO: ==> occupancy

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false")
    private DateTime reportedAt;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String reportedBy;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigDecimal amount;

    /**
     * TODO: split into turnoverGrossAmount ? turnoverNetAmount ? (turnoverVatAmount = 0 or null in ECPDW)
     * Should we have specific Occupancy (anticipating multiple per lease)? Or is lease - level OK?
     * Comments?
     * NonComparable Flag?
     * TurnoverPurchase count (tickets)? Or on separate entity?
     *
     * Two types of turnovers (prelim / audited)
     */

    @Getter @Setter
    @Column(name = "currencyId", allowsNull = "false")
    private Currency currency;

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }
}
