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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.util.Iterator;
import java.util.SortedSet;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.PhoneOrFaxNumber;
import org.estatio.dom.communicationchannel.PhoneOrFaxNumbers;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class PhoneOrFaxNumbersTest extends EstatioIntegrationTest {

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
    PhoneOrFaxNumbers phoneOrFaxNumbers;

    @Inject
    CommunicationChannels communicationChannels;

    @Inject
    Parties parties;

    Party party;

    CommunicationChannel communicationChannel;

    PhoneOrFaxNumber phoneOrFaxNumber;

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
        SortedSet<CommunicationChannel> results = communicationChannels.findByOwner(party);
        Iterator<CommunicationChannel> it = results.iterator();
        while (it.hasNext()) {
            CommunicationChannel next = it.next();
            if (next.getType() == CommunicationChannelType.PHONE_NUMBER) {
                phoneOrFaxNumber = (PhoneOrFaxNumber) next;
            }
        }

        assertThat(phoneOrFaxNumber.getPhoneNumber(), is("+31202211333"));
    }

    public static class FindByPhoneOrFaxNumber extends PhoneOrFaxNumbersTest {

        @Test
        public void happyCase() throws Exception {
            // when
            PhoneOrFaxNumber result = phoneOrFaxNumbers.findByPhoneOrFaxNumber(party, phoneOrFaxNumber.getPhoneNumber());

            // then
            assertThat(result, is(phoneOrFaxNumber));
        }
    }
}