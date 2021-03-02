package org.estatio.module.lease.dom.party;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.DomainObject;
import org.estatio.module.lease.dom.Lease;
import org.incode.module.base.dom.types.MoneyType;
import org.joda.time.LocalDate;

import javax.jdo.annotations.*;
import java.math.BigDecimal;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Unique(name = "EntryValueForLease__continuationPlanEntry_leaseDetails_UNQ", members = {"continuationPlanEntry", "leaseDetails"})
@DomainObject(objectType = "party.EntryValueForLease")
public class EntryValueForLease {

//    public String title(){
//        return TitleBuilder.start().withParent(getTenant()).withName(getStatus()).toString();
//    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "continuationPlanEntryId")
    private ContinuationPlanEntry continuationPlanEntry;

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationLeaseDetailsId")
    private TenantAdministrationLeaseDetails leaseDetails;

    @Getter @Setter
    @Column(allowsNull = "false", scale = MoneyType.Meta.SCALE)
    private BigDecimal amount;

}
