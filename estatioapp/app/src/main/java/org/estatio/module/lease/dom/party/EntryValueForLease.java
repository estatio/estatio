package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;

import org.apache.isis.applib.annotation.DomainObject;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.utils.TitleBuilder;

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

    @Override
    public int compareTo(final Object o) {
        EntryValueForLease casted = (EntryValueForLease) o;
        return getLeaseDetails().getLease().compareTo(casted.getLeaseDetails().getLease());
    }
}
