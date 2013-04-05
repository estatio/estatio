package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.PartiesJdo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class PartyIntegrationTest {

    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }

    @Test
    public void partyCanBeFound() throws Exception {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Assert.assertEquals(parties.findPartyByReference("HELLOWORLD").getReference(), "HELLOWORLD");
    }

    @Test
    public void partyCanBeFoundOnPartialReference() {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Assert.assertThat(parties.findParties("*LLOWOR*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName1() {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Assert.assertThat(parties.findParties("*ello Wor*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName2() {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Assert.assertThat(parties.findParties("Doe, Jo*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundCaseInsensitive() {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Assert.assertThat(parties.findParties("*OE, jO*").size(), is(1));
    }

    @Test
    public void partyHasFourCommunicationChannels() throws Exception {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Party party = parties.findPartyByReference("HELLOWORLD");
        Assert.assertThat(party.getCommunicationChannels().size(), is(4));
    }

    @Test
    public void partyHasOneFinancialAccount() throws Exception {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Party party = parties.findPartyByReference("HELLOWORLD");
        Assert.assertThat(party.getAccounts().size(), is(1));
    }

    
}
