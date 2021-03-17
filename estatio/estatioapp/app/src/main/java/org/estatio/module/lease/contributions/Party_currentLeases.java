package org.estatio.module.lease.contributions;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;

import javax.inject.Inject;
import java.util.List;

@Mixin(method = "coll")
public class Party_currentLeases {
    private final Party party;

    public Party_currentLeases(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table")
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Lease> coll() {
        return partyService.currentLeases(party);
    }

    @Inject
    PartyService partyService;
}
