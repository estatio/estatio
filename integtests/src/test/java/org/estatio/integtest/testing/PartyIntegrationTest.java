package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.FinancialAccountsJdo;
import org.estatio.jdo.PartiesJdo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PartyIntegrationTest {

    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();
    private Parties parties;
    private FinancialAccounts financialAccounts;

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }

    @Before
    public void setUp() throws Exception {
        parties = getIsft().getService(PartiesJdo.class);
        financialAccounts = getIsft().getService(FinancialAccountsJdo.class);
    }
    
    @Test
    public void partyCanBeFound() throws Exception {
        Assert.assertEquals(parties.findPartyByReference("HELLOWORLD").getReference(), "HELLOWORLD");
    }

    @Test
    public void partyCanBeFoundOnPartialReference() {
        Assert.assertThat(parties.findParties("*LLOWOR*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName1() {
        Assert.assertThat(parties.findParties("*ello Wor*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName2() {
        Assert.assertThat(parties.findParties("Doe, Jo*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundCaseInsensitive() {
        Assert.assertThat(parties.findParties("*OE, jO*").size(), is(1));
    }

    @Test
    public void partyHasFourCommunicationChannels() throws Exception {
        Party party = parties.findPartyByReference("HELLOWORLD");
        Assert.assertThat(party.getCommunicationChannels().size(), is(4));
    }

    @Test
    public void partyHasOneFinancialAccount() throws Exception {
        final Party party = parties.findPartyByReference("HELLOWORLD");

        List<FinancialAccount> allAccounts = financialAccounts.allAccounts();
        List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>(){
            public boolean apply(FinancialAccount fa) {
                return fa.getOwner() == party;
            }
        }));
        
        Assert.assertThat(partyAccounts.size(), is(1));
    }

}
