package com.eurocommercialproperties.estatio.jdo;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.query.QueryDefault;

public class PartiesJdo extends Parties {

    public List<Party> findPartiesByReference(final String reference) {
        return allMatches(queryForFindPartyByReference(reference));
    }
    
    public Party findPartyByReference(final String reference) {
        return firstMatch(queryForFindPartyByReference(reference));
    }
    
    private static QueryDefault<Party> queryForFindPartyByReference(String reference) {
        return new QueryDefault<Party>(Party.class, "parties_findPartyByReference", "r", StringUtils.wildcardToRegex(reference));
    }
     
}
