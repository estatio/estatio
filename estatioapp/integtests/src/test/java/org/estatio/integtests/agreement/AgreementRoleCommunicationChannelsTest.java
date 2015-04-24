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
import org.estatio.dom.agreement.AgreementRoleCommunicationChannels;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AgreementRoleCommunicationChannelsTest extends EstatioIntegrationTest {

    public static class FindByCommunicationChannel extends AgreementRoleCommunicationChannelsTest {

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
        CommunicationChannels communicationChannels;

        @Inject
        AgreementRoleCommunicationChannels agreementRoleCommunicationChannels;

        @Inject
        Parties parties;

        CommunicationChannel communicationChannel;

        Party party;

        @Before
        public void setUp() throws Exception {
            party = parties.findPartyByReference(_LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
            communicationChannel = communicationChannels.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).first();
        }


        @Test
        public void happyCase() throws Exception {
            assertThat(agreementRoleCommunicationChannels.findByCommunicationChannel(communicationChannel).get(0).getCommunicationChannel(), is(communicationChannel));
        }
    }

}
