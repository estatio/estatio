package org.estatio.module.lease.dom.party;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.estatio.module.lease.dom.Lease;
import org.incode.module.base.dom.types.MoneyType;

import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Unique(name = "TenantAdministrationLeaseDetails__tenantAdministrationStatus_lease_UNQ", members = {"tenantAdministrationStatus", "lease"})
@DomainObject(objectType = "party.TenantAdministrationLeaseDetails")
public class TenantAdministrationLeaseDetails {

//    public String title(){
//        return TitleBuilder.start().withParent(getTenant()).withName(getStatus()).toString();
//    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationStatusId")
    private TenantAdministrationStatus tenantAdministrationStatus;

    @Getter @Setter
    @Column(allowsNull = "false", name = "leaseId")
    private Lease lease;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal declaredAmountOfClaim;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean debtAdmitted;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal admittedAmountOfClaim;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean leaseContinued;

    @Persistent(mappedBy = "leaseDetails", dependentElement = "true")
    @Getter @Setter
    private SortedSet<EntryValueForLease> entryValuesForLease = new TreeSet<>();

}
