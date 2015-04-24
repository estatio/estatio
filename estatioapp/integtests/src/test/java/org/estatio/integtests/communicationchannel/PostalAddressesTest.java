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
import org.estatio.dom.communicationchannel.PostalAddress;
import org.estatio.dom.communicationchannel.PostalAddresses;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class PostalAddressesTest extends EstatioIntegrationTest {

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
    PostalAddresses postalAddresses;

    @Inject
    CommunicationChannels communicationChannels;

    @Inject
    Parties parties;

    Party party;

    CommunicationChannel communicationChannel;

    PostalAddress postalAddress;

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
        SortedSet<CommunicationChannel> results = communicationChannels.findByOwner(party);
        Iterator<CommunicationChannel> it = results.iterator();
        while (it.hasNext()) {
            CommunicationChannel next = it.next();
            if (next.getType() == CommunicationChannelType.POSTAL_ADDRESS) {
                postalAddress = (PostalAddress) next;
            }
        }

        assertThat(postalAddress.getAddress1(), is("1 Circle Square"));
        assertThat(postalAddress.getPostalCode(), is("W2AXXX"));
    }

    public static class FindByAddress extends PostalAddressesTest {

        @Test
        public void happyCase() throws Exception {
            // when
            PostalAddress result = postalAddresses.findByAddress(party,
                    postalAddress.getAddress1(),
                    postalAddress.getPostalCode(),
                    postalAddress.getCity(),
                    postalAddress.getCountry());

            // then
            assertThat(result, is(postalAddress));
        }
    }
}