package org.estatio.integtest.testing;


import static org.hamcrest.CoreMatchers.is;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.estatio.dom.party.Parties;
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
    public void partyCanBeFoundOnPartialName() {
        Parties parties = getIsft().getService(PartiesJdo.class);
        Assert.assertThat(parties.findParties("*ello Wor*").size(), is(1));
    }

}
