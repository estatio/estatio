package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.lease.contributions.PartyService;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTenant", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.TenantAdministrationRecord "
                        + "WHERE tenant == :tenant"),
        @javax.jdo.annotations.Query(
                name = "findByTenantAndStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.TenantAdministrationRecord "
                        + "WHERE tenant == :tenant && "
                        + "status == :status "),
})
@Unique(name = "TenantAdministrationRecord_tenant_status_UNQ", members = {"tenant", "status"})
@DomainObject(objectType = "party.TenantAdministrationRecord")
public class TenantAdministrationRecord {

    public String title(){
        return TitleBuilder.start().withParent(getTenant()).withName(getStatus()).toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "partyId")
    private Party tenant;

    @Getter @Setter
    @Column(allowsNull = "false")
    private AdministrationStatus status;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate judicialRedressDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate liquidationDate;

    public boolean hideLiquidationDate(){
        return getStatus()!=AdministrationStatus.LIQUIDATION;
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate statusChangedDate;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TenantAdministrationRecord changeJudicialRedressDate(final LocalDate date) {
        setJudicialRedressDate(date);
        return this;
    }

    public LocalDate default0ChangeJudicialRedressDate(){
        return getJudicialRedressDate();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public String getDescription(){
        return getStatus().getDescription();
    }

    @Getter @Setter
    @Column(allowsNull = "true", name = "previousId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private TenantAdministrationRecord previous;

    @Getter @Setter
    @Column(allowsNull = "true", name = "nextId")
    private TenantAdministrationRecord next;

    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    @PropertyLayout(multiLine = 5, hidden = Where.ALL_TABLES, promptStyle = PromptStyle.INLINE)
    @Column(allowsNull = "true", length = NotesType.Meta.MAX_LEN)
    private String comments;

    public ContinuationPlan getContinuationPlan() {
        return continuationPlanRepository.findUnique(this);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public ContinuationPlan createContinuationPlan(final LocalDate judgmentDate){
        return continuationPlanRepository.findOrCreate(this, judgmentDate);
    }

    public boolean hideCreateContinuationPlan(){
        return getContinuationPlan()!=null;
    }

    @Persistent(mappedBy = "tenantAdministrationRecord", dependentElement = "true")
    @Getter @Setter
    private SortedSet<TenantAdministrationLeaseDetails> leaseDetails = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public TenantAdministrationRecord addLeaseDetails(
            final Lease lease,
            final BigDecimal declaredAmountOfClaim,
            @Nullable final Boolean debtAdmitted,
            @Nullable final BigDecimal admittedAmountOfClaim,
            @Nullable final Boolean leaseContinued){
        tenantAdministrationLeaseDetailsRepository.upsert(this, lease, declaredAmountOfClaim, debtAdmitted, admittedAmountOfClaim, leaseContinued);
        return this;
    }

    public List<Lease> choices0AddLeaseDetails(){
        return partyService.allLeases(getTenant());
    }

    @Inject
    TenantAdministrationLeaseDetailsRepository tenantAdministrationLeaseDetailsRepository;

    @Inject
    ContinuationPlanRepository continuationPlanRepository;

    @Inject
    PartyService partyService;

}
