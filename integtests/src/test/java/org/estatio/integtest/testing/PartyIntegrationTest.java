package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.party.Party;
import org.junit.Assert;
import org.junit.Test;

public class PartyIntegrationTest extends AbstractEstatioIntegrationTest {

    @Test
    public void partyCanBeFound() throws Exception {
        Assert.assertNotNull(parties.findPartyByReference("HELLOWORLD"));
    }

    @Test
    public void partyCanNotBeFound() throws Exception {
        Assert.assertNull(parties.findPartyByReference("HELLO"));
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
        List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
            public boolean apply(FinancialAccount fa) {
                return fa.getOwner() == party;
            }
        }));
        Assert.assertThat(partyAccounts.size(), is(1));
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

}
