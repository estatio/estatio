package org.estatio.module.lease.dom.party;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Party;

@Mixin(method = "act")
public class Party_changeAdministrationStatus {
    private final Party party;

    public Party_changeAdministrationStatus(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Party act(final AdministrationStatus status, @Nullable final LocalDate judicialRedressDate) {
        tenantAdministrationRecordRepository.upsertOrCreateNext(status, party, judicialRedressDate);
        return party;
    }

    public boolean hideAct(){
        return !party.isTenant();
    }


    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;
}
