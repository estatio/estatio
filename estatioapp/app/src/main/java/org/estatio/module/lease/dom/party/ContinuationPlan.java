package org.estatio.module.lease.dom.party;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.joda.time.LocalDate;

import javax.jdo.annotations.*;
import java.util.SortedSet;
import java.util.TreeSet;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Unique(name = "ContinuationPlan_tenantAdministrationStatus_UNQ", members = {"tenantAdministrationStatus"})
@DomainObject(objectType = "party.ContinuationPlan")
public class ContinuationPlan {

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationStatusId")
    private TenantAdministrationStatus tenantAdministrationStatus;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate judgmentDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String duration;

    @Persistent(mappedBy = "continuationPlan", dependentElement = "true")
    @Getter @Setter
    private SortedSet<ContinuationPlanEntry> entries = new TreeSet<>();


}
