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

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;
import org.estatio.module.party.integtests.PartyModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;

public class CommunicationChannelRepository_IntegTest extends PartyModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.toBuilderScript());
            }
        });
    }

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Inject
    PartyRepository partyRepository;

    Party party;

    @Before
    public void setUp() throws Exception {
        party = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
    }

    public static class FindByOwner extends CommunicationChannelRepository_IntegTest {
        @Test
        public void happyCase() throws Exception {
            Assert.assertThat(communicationChannelRepository.findByOwner(party).size(), is(5));
        }
    }

    public static class FindByOwnerAndType extends CommunicationChannelRepository_IntegTest {
        @Test
        public void happyCase() throws Exception {
            Assert.assertThat(communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).size(), is(2));
        }
    }

    public static class FindOtherByOwnerAndType extends CommunicationChannelRepository_IntegTest {
        @Test
        public void happyCase() throws Exception {
            CommunicationChannel exclude = communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).first();
            Assert.assertThat(communicationChannelRepository.findOtherByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS, exclude).size(), is(1));
        }
    }
}