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

import java.util.SortedSet;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class CommunicationChannelTest extends EstatioIntegrationTest {

    public static class Remove extends CommunicationChannelTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                }
            });
        }

        @Inject
        private Parties parties;

        @Inject
        private CommunicationChannels communicationChannels;

        private Party party;

        private CommunicationChannel communicationChannel;

        private CommunicationChannel communicationChannelReplacement;

        @Before
        public void setUp() throws Exception {
            party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
            final SortedSet<CommunicationChannel> postalAddresses = communicationChannels.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);
            assertThat(postalAddresses.size(), is(2));
            communicationChannel = postalAddresses.first();
            communicationChannelReplacement = postalAddresses.last();
            assertNotEquals(communicationChannel, communicationChannelReplacement);
        }

        @org.junit.Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void withoutReplacement() throws Exception {
            expectedException.expectMessage("Communication channel is being used: provide a replacement");
            wrap(communicationChannel).remove(null);
        }

        @Test
        public void withReplacement() throws Exception {
            wrap(communicationChannel).remove(communicationChannelReplacement);
            final SortedSet<CommunicationChannel> postalAddresses = communicationChannels.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);
            assertThat(postalAddresses.size(), is(1));
            assertThat(postalAddresses.first(), is(communicationChannelReplacement));
        }

    }
}