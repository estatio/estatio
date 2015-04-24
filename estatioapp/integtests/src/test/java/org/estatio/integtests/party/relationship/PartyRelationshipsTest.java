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
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractInteractionEvent.Phase;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Party.RemoveEvent;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.Persons;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipType;
import org.estatio.dom.party.relationship.PartyRelationships;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PartyRelationshipsTest extends EstatioIntegrationTest {

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
    PartyRelationships relationships;

    @Inject
    Parties parties;

    @Inject
    Persons persons;

    @Inject
    CommunicationChannels communicationChannels;

    public static class AllRelationships extends PartyRelationshipsTest {
        @Test
        public void test() throws Exception {
            assertThat(relationships.allRelationships().size(), is(1));
        }

    }

    public static class FindByParty extends PartyRelationshipsTest {
        @Test
        public void findFrom() throws Exception {
            final List<PartyRelationship> results = relationships.findByParty(parties.findPartyByReference(OrganisationForTopModelGb.REF));
            assertThat(results.size(), is(1));
        }

        @Test
        public void findto() throws Exception {
            final List<PartyRelationship> results = relationships.findByParty(parties.findPartyByReference(PersonForGinoVannelliGb.REF));
            assertThat(results.size(), is(1));
        }
        
    }

    public static class NewRelationship extends PartyRelationshipsTest {

        @Test
        public void happyCase() throws Exception {
            final Party fromParty = parties.findPartyByReference(OrganisationForTopModelGb.REF);
            final Party toParty = parties.findPartyByReference(PersonForLinusTorvaldsNl.REF);
            PartyRelationship relationship = relationships.newRelationship(
                    fromParty,
                    toParty,
                    PartyRelationshipType.EMPLOYMENT.toTitle(), null);
            assertThat(relationship.getFrom(), is(fromParty));
            assertThat(relationship.getTo(), is(toParty));
            assertThat(relationship.getRelationshipType().toTitle(), is(PartyRelationshipType.EMPLOYMENT.toTitle()));
        }
    }

    public static class NewRelatedPerson extends PartyRelationshipsTest {

        private static final String JLOPEZ_EXAMPLE_COM = "jlopez@example.com";
        private static final String _555_12345 = "555-12345";
        private static final String J = "J";
        private static final String LOPEZ = "Lopez";
        private static final String JENNIFER = "Jennifer";

        @Test
        public void happyCase() throws Exception {
            final Party husband = parties.findPartyByReference(PersonForGinoVannelliGb.REF);
            PartyRelationship relationship = relationships.newRelatedPerson(husband, LOPEZ, J, JENNIFER, LOPEZ, PersonGenderType.FEMALE, PartyRelationshipType.MARRIAGE.toTitle(), null, _555_12345, JLOPEZ_EXAMPLE_COM);
            Person wife = (Person) relationship.getTo();
            assertThat(wife.getReference(), is(LOPEZ));
            assertThat(wife.getFirstName(), is(JENNIFER));
            assertThat(wife.getInitials(), is(J));
            assertThat(wife.getLastName(), is(LOPEZ));
            assertThat(communicationChannels.findByOwner(wife).size(), is(2));
            assertThat(communicationChannels.findByOwnerAndType(wife, CommunicationChannelType.EMAIL_ADDRESS).first().getName(), is(JLOPEZ_EXAMPLE_COM));
            assertThat(communicationChannels.findByOwnerAndType(wife, CommunicationChannelType.PHONE_NUMBER).first().getName(), is(_555_12345));
        }
    }

    public static class OnPartyRemove extends PartyRelationshipsTest {

        @Test
        public void executingReplacesParty() throws Exception {
            // when
            final Party parent = parties.findPartyByReference(OrganisationForTopModelGb.REF);
            final Party currentChild = parties.findPartyByReference(PersonForGinoVannelliGb.REF);
            final Party replacementChild = persons.newPerson("TEST", "JR", "JR", "Ewing", PersonGenderType.MALE, currentChild.getApplicationTenancy());
            Party.RemoveEvent event = new RemoveEvent(currentChild, null, replacementChild);
            event.setPhase(Phase.VALIDATE);
            relationships.on(event);
            event.setPhase(Phase.EXECUTING);
            relationships.on(event);

            // then
            assertThat(relationships.findByParty(parent).get(0).getTo(), is(replacementChild));
            assertThat(relationships.findByParty(parent).get(0).getTo(), is(replacementChild));
        }

    }
}
