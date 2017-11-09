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

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForJohnDoeNl;
import org.estatio.integtests.EstatioIntegrationTest;

public class Person_IntegTest extends EstatioIntegrationTest {

    public static class SetName extends Person_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonAndRolesForJohnDoeNl());
                }
            });
        }

        @Inject
        private PartyRepository partyRepository;

        private Person personJoeDoe;

        @Before
        public void setUp() throws Exception {
            personJoeDoe = (Person) partyRepository.findParties("Doe, Jo*").get(0);
        }

        @Test
        public void cannotModifyName() throws Exception {
            expectedExceptions.expectMessage("Cannot be updated directly; derived from first and last names");
            wrap(personJoeDoe).setName("Cannot change name directly");
        }

    }
}