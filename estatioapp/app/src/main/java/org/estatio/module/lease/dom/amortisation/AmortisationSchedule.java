package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.LeaseItem;

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
                name = "findByLeaseItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationSchedule "
                        + "WHERE leaseItem == :leaseItem "),
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationSchedule "
                        + "WHERE leaseItem == :leaseItem "
                        + "&& startDate == :startDate "),
})
@Unique(name = "AmortisationSchedule_leaseItem_startDate_UNQ", members = { "leaseItem", "startDate" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "amortisation.AmortisationSchedule"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class AmortisationSchedule extends UdoDomainObject2<AmortisationSchedule> {

    public AmortisationSchedule() {
        super("leaseItem, startDate");
    }

    public AmortisationSchedule(
            final LeaseItem leaseItem,
            final BigDecimal scheduledAmount,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        this();
        this.leaseItem = leaseItem;
        this.scheduledAmount = scheduledAmount;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getLeaseItem())
                .withName("Schedule")
                .withName(getInterval())
                .toString();
    }

    @Getter @Setter
    @Column(name = "leaseItemId", allowsNull = "false")
    private LeaseItem leaseItem;

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

    @Property(hidden = Where.EVERYWHERE)
    private LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Column(allowsNull = "true", length = NotesType.Meta.MAX_LEN)
    @PropertyLayout(multiLine = 5, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String note;

    @Persistent(mappedBy = "schedule", dependentElement = "true")
    @Getter @Setter
    private SortedSet<AmortisationEntry> entries = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public AmortisationSchedule createAndDistributeEntries(){
        amortisationScheduleService.createAndDistributeEntries(this);
        return this;
    }

    public boolean hideCreateAndDistributeEntries(){
        return !getEntries().isEmpty();
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getLeaseItem().getLease().getApplicationTenancy();
    }

    @Inject
    AmortisationScheduleService amortisationScheduleService;
}
