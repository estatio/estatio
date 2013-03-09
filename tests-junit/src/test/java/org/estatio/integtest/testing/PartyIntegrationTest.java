package org.estatio.integtest.testing;

import junit.framework.Assert;

import org.estatio.dom.party.Parties;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.PartiesJdo;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

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
}
