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
    public List<Party> findPartiesByReference(@Named("searchPattern") final String reference) {
        return allMatches(queryForFindPartyByReference(reference));
    }
    
    @Override
    public Party findPartyByReference(final String reference) {
        return firstMatch(queryForFindPartyByReference(reference));
    }
    
    private static QueryDefault<Party> queryForFindPartyByReference(String reference) {
        return new QueryDefault<Party>(Party.class, "parties_findPartyByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

    private static QueryDefault<Party> queryForFindParties(String reference) {
        return new QueryDefault<Party>(Party.class, "parties_findParties", "reference", StringUtils.wildcardToRegex(reference));
    }

}
