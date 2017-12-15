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
package org.estatio.module.lease.integtests.communicationchannel;

import java.util.SortedSet;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;

public class CommunicationChannel_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class Remove extends CommunicationChannel_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        @Inject
        PartyRepository partyRepository;

        @Inject
        CommunicationChannelRepository communicationChannelRepository;

        private Party party;

        private CommunicationChannel communicationChannel;

        private CommunicationChannel communicationChannelReplacement;

        @Before
        public void setUp() throws Exception {
            party = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
            final SortedSet<CommunicationChannel> postalAddresses = communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);
            Assert.assertThat(postalAddresses.size(), is(2));
            communicationChannel = postalAddresses.first();
            communicationChannelReplacement = postalAddresses.last();
            Assert.assertNotEquals(communicationChannel, communicationChannelReplacement);
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
            final SortedSet<CommunicationChannel> postalAddresses = communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);
            Assert.assertThat(postalAddresses.size(), is(1));
            Assert.assertThat(postalAddresses.first(), is(communicationChannelReplacement));
        }

    }
}