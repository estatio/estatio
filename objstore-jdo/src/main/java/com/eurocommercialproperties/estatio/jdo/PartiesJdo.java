package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.query.QueryDefault;

public class PartiesJdo extends Parties {

    @Hidden
    public Party findPartyByReference(final String reference) {
        return firstMatch(queryForFindPartyByReference(reference));
    }
    
    private static QueryDefault<Party> queryForFindPartyByReference(String reference) {
        return new QueryDefault<Party>(Party.class, "parties_findPartyByReference", "r", matches(reference));
    }

    private static String matches(final String reference) {
        return ".*" + reference.toUpperCase() + ".*";
    }

}
