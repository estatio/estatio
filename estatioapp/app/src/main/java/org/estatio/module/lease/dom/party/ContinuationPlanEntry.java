package org.estatio.module.lease.dom.party;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.DomainObject;
import org.joda.time.LocalDate;

import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@DomainObject(objectType = "party.ContinuationPlanEntry")
public class ContinuationPlanEntry {

//    public String title(){
//        return TitleBuilder.start().withParent(getTenant()).withName(getStatus()).toString();
//    }

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

}
