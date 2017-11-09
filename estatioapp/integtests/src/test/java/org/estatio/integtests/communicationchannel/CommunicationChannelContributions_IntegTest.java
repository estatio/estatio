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
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_newChannelContributions;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesBuilder;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;

public class CommunicationChannelContributions_IntegTest extends EstatioIntegrationTest {

    @Inject
    CommunicationChannels communicationChannels;
    @Inject
    CommunicationChannelOwner_newChannelContributions communicationChannelContributions;

    @Inject
    PartyRepository partyRepository;

    Party party;

    public static class CommunicationChannels extends CommunicationChannelContributions_IntegTest {

        @Before
        public void setUp() throws Exception {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new OrganisationForHelloWorldGb());
                }
            });
            party = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
        }

        @Test
        public void fixtureData() throws Exception {
            Assert.assertThat(communicationChannelContributions.communicationChannels(party).size(), is(5));
        }
    }

    public static class NewEmail extends CommunicationChannelContributions_IntegTest {

        private PersonAndRolesBuilder fs;

        @Before
        public void setup() {
            fs = new PersonAndRolesBuilder();

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, fs);
                }
            });
        }

        @Test
        public void happyCase() throws Exception {

            final Party party = fs.getPerson();

            // given
            final SortedSet<CommunicationChannel> before = communicationChannelContributions.communicationChannels(party);
            Assertions.assertThat(before).isEmpty();

            // when
            final String emailAddress = "bar@foo.com";
            wrap(communicationChannelContributions).newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);

            // then
            final SortedSet<CommunicationChannel> after = communicationChannelContributions.communicationChannels(party);
            Assertions.assertThat(after).hasSize(1);

            final CommunicationChannel communicationChannel = after.first();
            Assertions.assertThat(communicationChannel).isInstanceOf(EmailAddress.class);
            Assertions.assertThat(((EmailAddress)communicationChannel).getEmailAddress()).isEqualTo(emailAddress);
        }
    }
}