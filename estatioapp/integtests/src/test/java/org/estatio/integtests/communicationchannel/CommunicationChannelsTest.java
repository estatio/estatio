/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integtests.communicationchannel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class CommunicationChannelsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForTopModelGb());
            }
        });
    }

    @Inject
    CommunicationChannels communicationChannels;

    @Inject
    Parties parties;

    Party party;

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
    }

    public static class FindByOwner extends CommunicationChannelsTest {
        @Test
        public void happyCase() throws Exception {
            Assert.assertThat(communicationChannels.findByOwner(party).size(), is(5));
        }
    }

    public static class FindByOwnerAndType extends CommunicationChannelsTest {
        @Test
        public void happyCase() throws Exception {
            Assert.assertThat(communicationChannels.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).size(), is(2));
        }
    }

    public static class FindOtherByOwnerAndType extends CommunicationChannelsTest {
        @Test
        public void happyCase() throws Exception {
            CommunicationChannel exclude = communicationChannels.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).first();
            Assert.assertThat(communicationChannels.findOtherByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS, exclude).size(), is(1));
        }
    }
}