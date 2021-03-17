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
package org.estatio.module.lease.integtests.agreement;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AgreementRoleCommunicationChannelRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class FindByCommunicationChannel extends AgreementRoleCommunicationChannelRepository_IntegTest {

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
        CommunicationChannelRepository communicationChannelRepository;

        @Inject
        AgreementRoleCommunicationChannelRepository agreementRoleCommunicationChannelRepository;

        @Inject
        PartyRepository partyRepository;

        CommunicationChannel communicationChannel;

        Party party;

        @Before
        public void setUp() throws Exception {
            party = Lease_enum.OxfTopModel001Gb.getTenant_d().findUsing(serviceRegistry);
            communicationChannel = communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).first();
        }

        @Test
        public void happyCase() throws Exception {
            assertThat(
                    agreementRoleCommunicationChannelRepository.findByCommunicationChannel(communicationChannel).get(0).getCommunicationChannel(), is(communicationChannel));
        }
    }

}
