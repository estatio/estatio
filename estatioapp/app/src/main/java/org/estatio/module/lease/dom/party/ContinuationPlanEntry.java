package org.estatio.module.lease.dom.party;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
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
@DomainObject(objectType = "party.ContinuationPlanEntry")
public class ContinuationPlanEntry {

//    public String title(){
//        return TitleBuilder.start().withParent(getTenant()).withName(getStatus()).toString();
//    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationStatusId")
    private ContinuationPlan continuationPlan;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigDecimal percentage;

}
