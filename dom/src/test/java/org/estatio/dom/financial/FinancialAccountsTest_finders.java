package org.estatio.dom.financial;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class FinancialAccountsTest_finders {

    private FinderInteraction finderInteraction;

    private FinancialAccounts financialAccounts;

    private Party party;

    @Before
    public void setup() {
        
        party = new PartyForTesting();
        
        financialAccounts = new FinancialAccounts() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<FinancialAccount> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    @Test
    public void findAccountByReference() {

        financialAccounts.findAccountByReference("*REF?1*");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FinancialAccount.class));
        assertThat(finderInteraction.getQueryName(), is("findByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)".*REF.1.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }

    @Test
    public void findBankAccountsByParty() {
        
        financialAccounts.findBankAccountsByParty(party);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FinancialAccount.class));
        assertThat(finderInteraction.getQueryName(), is("findByTypeAndParty"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("owner"), is((Object)party));
        assertThat(finderInteraction.getArgumentsByParameterName().get("type"), is((Object)FinancialAccountType.BANK_ACCOUNT));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }

    @Test
    public void allAccounts() {
        
        financialAccounts.allAccounts();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
