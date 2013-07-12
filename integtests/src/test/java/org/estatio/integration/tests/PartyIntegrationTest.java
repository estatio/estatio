/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integration.tests;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.EstatioIntegrationTest;

public class PartyIntegrationTest extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Test
    public void partyCanBeFound() throws Exception {
        Assert.assertNotNull(service(Parties.class).findPartyByReferenceOrName("HELLOWORLD"));
    }

    @Test
    public void partyCanNotBeFound() throws Exception {
        Assert.assertNull(service(Parties.class).findPartyByReferenceOrName("HELLO"));
    }

    @Test
    public void partyHasFourCommunicationChannels() throws Exception {
        Party party = service(Parties.class).findPartyByReferenceOrName("HELLOWORLD");
        Assert.assertThat(party.getCommunicationChannels().size(), is(4));
    }

    @Test
    public void partyHasOneFinancialAccount() throws Exception {
        final Party party = service(Parties.class).findPartyByReferenceOrName("HELLOWORLD");
        List<FinancialAccount> allAccounts = service(FinancialAccounts.class).allAccounts();
        List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
            public boolean apply(FinancialAccount fa) {
                return fa.getOwner() == party;
            }
        }));
        Assert.assertThat(partyAccounts.size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialReference() {
        Assert.assertThat(service(Parties.class).findParties("*LLOWOR*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName1() {
        Assert.assertThat(service(Parties.class).findParties("*ello Wor*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName2() {
        Assert.assertThat(service(Parties.class).findParties("Doe, Jo*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundCaseInsensitive() {
        Assert.assertThat(service(Parties.class).findParties("*OE, jO*").size(), is(1));
    }

}
