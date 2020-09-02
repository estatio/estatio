package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationSchedule"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationSchedule "
                        + "WHERE lease == :lease "),
})
@Unique(name = "AmortisationSchedule_lease_charge_startDate_UNQ", members = { "lease", "charge", "startDate" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "amortisation.AmortisationSchedule"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class AmortisationSchedule extends UdoDomainObject2<AmortisationSchedule> {

    public AmortisationSchedule() {
        super("lease, charge, startDate");
    }

    @Getter @Setter
    @Column(name = "leaseId", allowsNull = "false")
    private Lease lease;

    @Getter @Setter
    @Column(name = "chargeId", allowsNull = "false")
    private Charge charge;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal scheduledAmount;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Frequency frequency;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate endDate;

    @Persistent(mappedBy = "schedule", dependentElement = "true")
    @Getter @Setter
    private SortedSet<AmortisationEntry> entries = new TreeSet<>();

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }
}
