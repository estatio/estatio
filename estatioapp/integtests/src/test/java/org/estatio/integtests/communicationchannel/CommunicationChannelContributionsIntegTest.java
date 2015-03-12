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
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.PersonBuilder;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;

public class CommunicationChannelContributionsIntegTest extends EstatioIntegrationTest {

    @Inject
    CommunicationChannels communicationChannels;
    @Inject
    CommunicationChannelContributions communicationChannelContributions;

    @Inject
    Parties parties;

    Party party;

    public static class CommunicationChannels extends CommunicationChannelContributionsIntegTest {

        @Before
        public void setUp() throws Exception {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new OrganisationForHelloWorldGb());
                }
            });
            party = parties.findPartyByReference(OrganisationForHelloWorldGb.REF);
        }

        @Test
        public void fixtureData() throws Exception {
            Assert.assertThat(communicationChannelContributions.communicationChannels(party).size(), is(5));
        }
    }

    public static class NewEmail extends CommunicationChannelContributionsIntegTest {

        private PersonBuilder fs;

        @Before
        public void setup() {
            fs = new PersonBuilder();

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