package org.estatio.module.lease.dom.party;

import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

// TODO: annotations etc
@DomainObject(objectType = "party.TenantAdministrationStatus")
public class TenantAdministrationStatus {

    @Getter @Setter
    @Column(allowsNull = "false", name = "partyId")
    private Party tenant;

    @Getter @Setter
    @Column(allowsNull = "false")
    private AdministrationStatus status;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate judicialRedressDate;

    // TODO: changeDateMethod

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public String description(){
        return getStatus().getDescription();
    }

}
