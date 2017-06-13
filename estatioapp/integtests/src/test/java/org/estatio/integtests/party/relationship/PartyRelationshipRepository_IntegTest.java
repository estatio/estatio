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
package org.estatio.integtests.party.relationship;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Party.RemoveEvent;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipRepository;
import org.estatio.dom.party.relationship.PartyRelationshipTypeEnum;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PartyRelationshipRepository_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForTopModelGb());
                executionContext.executeChild(this, new PersonForGinoVannelliGb());
                executionContext.executeChild(this, new PersonForLinusTorvaldsNl());
            }
        });
    }

    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    public static class AllRelationships extends PartyRelationshipRepository_IntegTest {
        @Test
        public void test() throws Exception {
            assertThat(partyRelationshipRepository.allRelationships().size(), is(1));
        }

    }

    public static class FindByParty extends PartyRelationshipRepository_IntegTest {
        @Test
        public void findFrom() throws Exception {
            final List<PartyRelationship> results = partyRelationshipRepository.findByParty(partyRepository.findPartyByReference(OrganisationForTopModelGb.REF));
            assertThat(results.size(), is(1));
        }

        @Test
        public void findto() throws Exception {
            final List<PartyRelationship> results = partyRelationshipRepository.findByParty(partyRepository.findPartyByReference(PersonForGinoVannelliGb.REF));
            assertThat(results.size(), is(1));
        }

    }

    public static class NewRelationship extends PartyRelationshipRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            final Party fromParty = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
            final Party toParty = partyRepository.findPartyByReference(PersonForLinusTorvaldsNl.REF);
            PartyRelationship relationship = partyRelationshipRepository.newRelationship(
                    fromParty,
                    toParty,
                    PartyRelationshipTypeEnum.EMPLOYMENT.toTitle(), null);
            assertThat(relationship.getFrom(), is(fromParty));
            assertThat(relationship.getTo(), is(toParty));
            assertThat(relationship.getRelationshipType().toTitle(), is(PartyRelationshipTypeEnum.EMPLOYMENT.toTitle()));
        }
    }

    public static class NewRelatedPerson extends PartyRelationshipRepository_IntegTest {

        private static final String JLOPEZ_EXAMPLE_COM = "jlopez@example.com";
        private static final String _555_12345 = "555-12345";
        private static final String J = "J";
        private static final String LOPEZ = "Lopez";
        private static final String JENNIFER = "Jennifer";

        @Test
        public void happyCase() throws Exception {
            final Party husband = partyRepository.findPartyByReference(PersonForGinoVannelliGb.REF);
            PartyRelationship relationship = partyRelationshipRepository.newRelatedPerson(husband, LOPEZ, J, JENNIFER, LOPEZ, PersonGenderType.FEMALE, PartyRelationshipTypeEnum.MARRIAGE.toTitle(), null, _555_12345, JLOPEZ_EXAMPLE_COM);
            Person wife = (Person) relationship.getTo();
            assertThat(wife.getReference(), is(LOPEZ));
            assertThat(wife.getFirstName(), is(JENNIFER));
            assertThat(wife.getInitials(), is(J));
            assertThat(wife.getLastName(), is(LOPEZ));
            assertThat(communicationChannelRepository.findByOwner(wife).size(), is(2));
            assertThat(communicationChannelRepository.findByOwnerAndType(wife, CommunicationChannelType.EMAIL_ADDRESS).first().getName(), is(JLOPEZ_EXAMPLE_COM));
            assertThat(communicationChannelRepository.findByOwnerAndType(wife, CommunicationChannelType.PHONE_NUMBER).first().getName(), is(_555_12345));
        }
    }

    public static class OnPartyRemove extends PartyRelationshipRepository_IntegTest {

        @Test
        public void executingReplacesParty() throws Exception {
            // when
            final Party parent = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
            final Party currentChild = partyRepository.findPartyByReference(PersonForGinoVannelliGb.REF);
            final Party replacementChild = personRepository.newPerson("TEST", "JR", "JR", "Ewing", PersonGenderType.MALE, currentChild.getApplicationTenancy());
            Party.RemoveEvent event = new RemoveEvent();
            event.setSource(currentChild);
            event.setArguments(Lists.newArrayList(replacementChild));
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            partyRelationshipRepository.on(event);
            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTING);
            partyRelationshipRepository.on(event);

            // then
            assertThat(partyRelationshipRepository.findByParty(parent).get(0).getTo(), is(replacementChild));
        }

    }
}
