package org.estatio.module.lease.contributions;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;

import javax.inject.Inject;
import java.util.List;

@Mixin(method = "act")
public class Party_allLeases {
    private final Party party;

    public Party_allLeases(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "List All", contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "currentLeases", sequence = "1")
    public List<Lease> act() {
        return partyService.allLeases(party);
    }

    @Inject
    PartyService partyService;
}
