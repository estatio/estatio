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
package org.estatio.integtests.party;

import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import com.google.common.base.Throwables;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForAcme;
import org.estatio.fixture.party.OrganisationForTopModel;
import org.estatio.fixture.party.PersonForGinoVannelli;
import org.estatio.fixture.party.PersonForJohnDoe;
import org.estatio.integtests.EstatioIntegrationTest;

public class PartyTest extends EstatioIntegrationTest {

    @Inject
    Parties parties;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class Remove extends PartyTest {

        @Inject
        private DomainObjectContainer container;

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    execute(new EstatioBaseLineFixture(), executionContext);
                    // linked together:
                    execute(new OrganisationForTopModel(), executionContext);
                    execute(new PersonForGinoVannelli(), executionContext);
                    // only relationship
                    execute(new PersonForJohnDoe(), executionContext);
                    // only comm channels
                    execute(new OrganisationForAcme(), executionContext);
                }
            });
        }

        @Test
        public void happyCase() {
            Party party = parties.findPartyByReference(PersonForJohnDoe.PARTY_REFERENCE);
            wrap(party).remove();
            assertNull(parties.findPartyByReferenceOrNull(PersonForJohnDoe.PARTY_REFERENCE));
        }

        @Test
        public void whenVetoingSubscriber() {

            // then
            expectedException.expect(InvalidException.class);

            // when
            Party party = parties.findPartyByReference(PersonForGinoVannelli.PARTY_REFERENCE);
            wrap(party).remove();
        }

        @Test
        public void whenNoVetoingSubscriber() {

            // then
            expectedException.expect(causalChainHasMessageWith("constraint"));

            Party party = parties.findPartyByReference(OrganisationForAcme.PARTY_REFERENCE);
            wrap(party).remove();
        }

        private static Matcher<Throwable> causalChainHasMessageWith(final String messageFragment) {
            return new TypeSafeMatcher<Throwable>() {

                @Override
                public void describeTo(Description arg0) {
                    arg0.appendText("causal chain has message with " + messageFragment);

                }

                @Override
                protected boolean matchesSafely(Throwable arg0) {
                    for (Throwable ex : Throwables.getCausalChain(arg0)) {
                        if (ex.getMessage().contains(messageFragment)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

    }

}