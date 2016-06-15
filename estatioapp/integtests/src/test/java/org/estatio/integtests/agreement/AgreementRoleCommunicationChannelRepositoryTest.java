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
package org.estatio.integtests.agreement;

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AgreementRoleCommunicationChannelRepositoryTest extends EstatioIntegrationTest {

    public static class FindByCommunicationChannel extends AgreementRoleCommunicationChannelRepositoryTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
        }

        @Inject
        CommunicationChannelRepository communicationChannelRepository;

        @Inject
        AgreementRoleCommunicationChannelRepository agreementRoleCommunicationChannelRepository;

        @Inject
        Parties parties;

        CommunicationChannel communicationChannel;

        Party party;

        @Before
        public void setUp() throws Exception {
            party = parties.findPartyByReference(LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
            communicationChannel = communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).first();
        }


        @Test
        public void happyCase() throws Exception {
            assertThat(
                    agreementRoleCommunicationChannelRepository.findByCommunicationChannel(communicationChannel).get(0).getCommunicationChannel(), is(communicationChannel));
        }
    }

}
