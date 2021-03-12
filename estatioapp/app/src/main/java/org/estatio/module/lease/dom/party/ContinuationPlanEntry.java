package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;

import com.google.api.client.util.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.lease.dom.Lease;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.ContinuationPlanEntry "
                        + "WHERE continuationPlan == :continuationPlan && "
                        + "date == :date "),
})
@Unique(name = "ContinuationPlanEntry_continuationPlan_date_UNQ", members = {"continuationPlan", "date"})
@DomainObject(objectType = "party.ContinuationPlanEntry")
public class ContinuationPlanEntry implements Comparable{

    public String title(){
        return TitleBuilder.start().withParent(getContinuationPlan()).withName(getDate()).toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "continuationPlanId")
    private ContinuationPlan continuationPlan;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal percentage;

    @Persistent(mappedBy = "continuationPlanEntry", dependentElement = "true")
    @Getter @Setter
    private SortedSet<EntryValueForLease> entryValues = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public EntryValueForLease addValue(final Lease lease, final BigDecimal amount){
        final TenantAdministrationLeaseDetails leaseDetailsIfAny = tenantAdministrationLeaseDetailsRepository
                .findUnique(continuationPlan.getTenantAdministrationRecord(), lease);
        if (leaseDetailsIfAny==null) return null;
        return entryValueForLeaseRepository.upsert(this, leaseDetailsIfAny, amount);
    }

    public List<Lease> choices0AddValue(){
        return Lists.newArrayList(getContinuationPlan().getTenantAdministrationRecord().getLeaseDetails())
                .stream()
                .map(ld->ld.getLease())
                .collect(Collectors.toList());
    }

    public BigDecimal default1AddValue(final Lease lease){
        final TenantAdministrationLeaseDetails leaseDetailsIfAny = tenantAdministrationLeaseDetailsRepository
                .findUnique(continuationPlan.getTenantAdministrationRecord(), lease);
        if (leaseDetailsIfAny==null || leaseDetailsIfAny.getAdmittedAmountOfClaim()==null) return null;
        return entryValueForLeaseRepository.calculateAmount(leaseDetailsIfAny.getAdmittedAmountOfClaim(), getPercentage());
    }

    @Inject EntryValueForLeaseRepository entryValueForLeaseRepository;

    @Inject TenantAdministrationLeaseDetailsRepository tenantAdministrationLeaseDetailsRepository;

    @Override public int compareTo(final Object o) {
        ContinuationPlanEntry casted = (ContinuationPlanEntry) o;
        return this.getDate().compareTo(casted.getDate());
    }
}
