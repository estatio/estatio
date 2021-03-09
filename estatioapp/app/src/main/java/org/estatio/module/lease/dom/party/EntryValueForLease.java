package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
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

import org.incode.module.base.dom.types.MoneyType;
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
                        + "FROM org.estatio.module.lease.dom.party.EntryValueForLease "
                        + "WHERE continuationPlanEntry == :continuationPlanEntry && "
                        + "leaseDetails == :leaseDetails "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseDetails", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.EntryValueForLease "
                        + "WHERE leaseDetails == :leaseDetails "),
})
@Unique(name = "EntryValueForLease__continuationPlanEntry_leaseDetails_UNQ", members = {"continuationPlanEntry", "leaseDetails"})
@DomainObject(objectType = "party.EntryValueForLease")
public class EntryValueForLease implements Comparable{

    public String title(){
        return TitleBuilder.start().withParent(getContinuationPlanEntry()).withName(getLeaseDetails().getLease()).toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "continuationPlanEntryId")
    private ContinuationPlanEntry continuationPlanEntry;

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationLeaseDetailsId")
    private TenantAdministrationLeaseDetails leaseDetails;

    @Getter @Setter
    @Column(allowsNull = "false", scale = MoneyType.Meta.SCALE)
    private BigDecimal amount;

    @Getter @Setter
    @Column(allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    @PropertyLayout(promptStyle = PromptStyle.INLINE)
    private Boolean paid;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Lease getLease(){
        return getLeaseDetails().getLease();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public LocalDate getDate(){
        return getContinuationPlanEntry().getDate();
    }


    @Override
    public int compareTo(final Object o) {
        EntryValueForLease casted = (EntryValueForLease) o;
        return getLeaseDetails().getLease().compareTo(casted.getLeaseDetails().getLease());
    }
}
