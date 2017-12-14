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
package org.estatio.module.party.integtests.communicationchannel;

import java.util.Iterator;
import java.util.SortedSet;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.commchannel.PostalAddressRepository;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;
import org.estatio.module.party.integtests.PartyModuleIntegTestAbstract;

import static org.hamcrest.Matchers.is;

public class PostalAddressRepository_IntegTest extends PartyModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
            }
        });
    }

    @Inject
    PostalAddressRepository postalAddressRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    Party party;

    CommunicationChannel communicationChannel;

    PostalAddress postalAddress;

    @Before
    public void setUp() throws Exception {
        party = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        SortedSet<CommunicationChannel> results = communicationChannelRepository.findByOwner(party);
        Iterator<CommunicationChannel> it = results.iterator();
        while (it.hasNext()) {
            CommunicationChannel next = it.next();
            if (next.getType() == CommunicationChannelType.POSTAL_ADDRESS) {
                postalAddress = (PostalAddress) next;
            }
        }

        Assert.assertThat(postalAddress.getAddress1(), is("1 Circle Square"));
        Assert.assertThat(postalAddress.getPostalCode(), is("W2AXXX"));
    }

    public static class FindByAddress extends PostalAddressRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // when
            PostalAddress result = postalAddressRepository.findByAddress(party,
                    postalAddress.getAddress1(),
                    postalAddress.getPostalCode(),
                    postalAddress.getCity(),
                    postalAddress.getCountry());

            // then
            Assert.assertThat(result, is(postalAddress));
        }
    }
}