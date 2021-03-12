package org.estatio.module.lease.dom.party;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Party;

@Mixin(method = "act")
public class Party_changeAdministrationRecord {
    private final Party party;

    public Party_changeAdministrationRecord(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Party act(final AdministrationStatus status, @Nullable final LocalDate judicialRedressDate, @Nullable final LocalDate statusChangedDate) {
        tenantAdministrationRecordRepository.upsertOrCreateNext(status, party, judicialRedressDate);
        return party;
    }

    public LocalDate default2Act() { return clockService.now(); }

    public String validateAct(AdministrationStatus status, LocalDate judicialRedressDate, LocalDate statusChangedDate) {
        if (tenantAdministrationRecordRepository.findUnique(party, status) == null) {
            return statusChangedDate == null ? "Status changed date is mandatory when changing record with new status" : null;
        }
        return null;
    }

    public boolean hideAct(){
        return !party.isTenant();
    }


    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

    @Inject
    ClockService clockService;
}
