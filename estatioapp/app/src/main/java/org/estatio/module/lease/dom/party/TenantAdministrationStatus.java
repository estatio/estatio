package org.estatio.module.lease.dom.party;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Unique;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.utils.TitleBuilder;

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
                name = "findStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.TenantAdministrationStatus "
                        + "WHERE tenant == :tenant")
})
@Unique(name = "TenantAdministrationStatus_tenant_UNQ", members = {"tenant"})
@DomainObject(objectType = "party.TenantAdministrationStatus")
public class TenantAdministrationStatus {

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

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TenantAdministrationStatus changeJudicialRedressDate(final LocalDate date) {
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

}
