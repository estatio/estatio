package org.estatio.jdo;

import java.util.List;


import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

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
