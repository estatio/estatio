package org.estatio.jdo;

import java.util.List;

import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class PartiesJdo extends Parties {

    @Override
    public List<Party> findParties(@Named("searchPattern") final String searchPattern) {
        return allMatches(queryForFindParties(searchPattern));
    }

    @Override
    public List<Party> findPartiesByReference(@Named("searchPattern") final String searchPattern) {
        return allMatches(queryForFindPartyByReference(searchPattern));
    }
    
    @Override
    public Party findPartyByReference(@Named("searchPattern") final String searchPattern) {
        return firstMatch(queryForFindPartyByReference(searchPattern));
    }
    
    // //////////////////////////////////////
    
    private static QueryDefault<Party> queryForFindPartyByReference(String searchPattern) {
        return new QueryDefault<Party>(Party.class, "parties_findPartyByReference", "searchPattern", StringUtils.wildcardToRegex(searchPattern));
    }

    private static QueryDefault<Party> queryForFindParties(String searchPattern) {
        return new QueryDefault<Party>(Party.class, "parties_findParties", "searchPattern", StringUtils.wildcardToRegex(searchPattern));
    }

}
