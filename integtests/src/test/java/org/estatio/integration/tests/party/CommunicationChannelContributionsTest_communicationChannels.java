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
package org.estatio.integration.tests.party;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class CommunicationChannelContributionsTest_communicationChannels extends EstatioIntegrationTest {

    private Parties parties;
    private CommunicationChannelContributions communicationChannelContributions;
    private Party partyHelloWorld;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        parties = service(Parties.class);
        communicationChannelContributions = service(CommunicationChannelContributions.class);
        partyHelloWorld = parties.findPartyByReferenceOrName("HELLOWORLD");
    }
    
    @Test
    public void fixtureData() throws Exception {
        Assert.assertThat(communicationChannelContributions.communicationChannels(partyHelloWorld).size(), is(3));
    }
}
