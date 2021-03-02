package org.estatio.module.lease.dom.party;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;
import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.joda.time.LocalDate;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Unique;
import java.math.BigDecimal;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
//@Unique(name = "TenantAdministrationLeaseDetails_lease_UNQ", members = {"lease"})
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
    @Column(allowsNull = "true")
    private BigDecimal declaredAmountOfClaim;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean leaseContinued;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean admittedDebt;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal admittedAmountOfClaim;

}
