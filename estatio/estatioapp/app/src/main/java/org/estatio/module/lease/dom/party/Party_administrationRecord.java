package org.estatio.module.lease.dom.party;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Party;

@Mixin(method = "prop")
public class Party_administrationRecord {
    private final Party party;

    public Party_administrationRecord(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public TenantAdministrationRecord prop() {
        return tenantAdministrationRecordRepository.latestForParty(party);
    }

    public boolean hideProp(){
        return !party.isTenant();
    }

    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;
}
