package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
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
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.distribution.Distributable;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationEntry"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationEntry "
                        + "WHERE schedule == :schedule "
                        + "&& entryDate == :entryDate "),
        @Query(
                name = "findBySchedule", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationEntry "
                        + "WHERE schedule == :schedule "),
})
@Unique(name = "AmortisationEntry_schedule_entryDate_UNQ", members = { "schedule", "entryDate" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "amortisation.AmortisationEntry"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_CHILD
)
public class AmortisationEntry extends UdoDomainObject2<AmortisationSchedule> implements Distributable {

    public AmortisationEntry(){
        super("schedule, entryDate");
    }

    public AmortisationEntry(
            final AmortisationSchedule schedule,
            final LocalDate entryDate,
            final BigDecimal entryAmount) {
        this();
        this.schedule = schedule;
        this.entryDate = entryDate;
        this.entryAmount = entryAmount;
    }

    @Getter @Setter
    @Column(name = "scheduleId", allowsNull = "false")
    private AmortisationSchedule schedule;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate entryDate;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal entryAmount;

    @Override public ApplicationTenancy getApplicationTenancy() {
        return schedule.getApplicationTenancy();
    }

    @Override
    @Programmatic
    public BigDecimal getSourceValue() {
        return schedule.getScheduledAmount();
    }

    @Override
    @Programmatic
    public BigDecimal getValue() {
        return getEntryAmount();
    }

    @Override
    @Programmatic
    public void setValue(final BigDecimal value) {
        this.setEntryAmount(value);
    }
}
