package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

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
        @Query(
                name = "findByLeaseAndChargeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationSchedule "
                        + "WHERE lease == :lease "
                        + "&& charge == :charge "
                        + "&& startDate == :startDate "),
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amortisation.AmortisationSchedule "
                        + "WHERE lease == :lease "
                        + "&& charge == :charge "
                        + "&& startDate == :startDate "
                        + "&& sequence == :sequence "),
})
@Unique(name = "AmortisationSchedule_lease_charge_startDate_sequence_UNQ", members = { "lease", "charge", "startDate", "sequence" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "amortisation.AmortisationSchedule"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class AmortisationSchedule extends UdoDomainObject2<AmortisationSchedule> {

    public AmortisationSchedule() {
        super("lease, charge, startDate, sequence");
    }

    public AmortisationSchedule(
            final Lease lease,
            final Charge charge,
            final BigDecimal scheduledValue,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigInteger sequence){
        this();
        this.lease = lease;
        this.charge = charge;
        this.scheduledValue = scheduledValue;
        this.outstandingValue = scheduledValue;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sequence = sequence;
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getLease())
                .withName("schedule ")
                .withName(getCharge().getReference())
                .withName(getInterval())
                .toString();
    }

    @Getter @Setter
    @Column(name = "leaseId", allowsNull = "false")
    private Lease lease;

    @Getter @Setter
    @Column(name = "chargeId", allowsNull = "false")
    private Charge charge;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal scheduledValue;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal outstandingValue;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Frequency frequency;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate endDate;

    @Programmatic
    public LocalDateInterval getInterval() {
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

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public AmortisationSchedule redistributeEntries(){
        amortisationScheduleService.redistributeEntries(this);
        verifyOutstandingValue();
        return this;
    }

    public boolean hideRedistributeEntries(){
        final BigDecimal totalValueOfEntries = Lists.newArrayList(getEntries()).stream()
                .map(e -> e.getEntryAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalValueOfEntries.compareTo(getScheduledValue())==0;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void verifyOutstandingValue() {
        setOutstandingValue(
                getScheduledValue().subtract(
                    Lists.newArrayList(getEntries()).stream()
                    .filter(e->e.getDateReported()!=null)
                    .map(e->e.getEntryAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
        );
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private CreationStrategy creationStrategyUsed;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigInteger sequence;

    @Programmatic
    public boolean isReported(){
        for (AmortisationEntry entry : getEntries()){
            if (entry.getDateReported()!=null) return true;
        }
        return false;
    }

    @Programmatic
    public AmortisationSchedule appendTextToNote(final String text){
        if (getNote()==null || getNote().length()==0){
            setNote(text);
        } else {
            setNote(getNote() + " | " + text);
        }
        return this;
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }

    @Inject
    AmortisationScheduleService amortisationScheduleService;
}
