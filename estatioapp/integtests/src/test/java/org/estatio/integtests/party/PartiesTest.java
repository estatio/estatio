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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PartiesTest extends EstatioIntegrationTest {

    @Inject
    Parties parties;


    public static class FindParties extends PartiesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonForJohnDoeNl());
                    executionContext.executeChild(this, new OrganisationForHelloWorldNl());
                }
            });
        }

        @Test
        public void partialReference_wildcardsBothEnds() {
            assertThatFindPartiesSizeIs("*LLOWOR*", 1);
        }

        @Test
        public void partialName_wildcardsBothEnds() {
            assertThatFindPartiesSizeIs("*ello Wor*", 1);
        }

        @Test
        public void partialName_wildcardsAtOneEndOnly() {
            assertThatFindPartiesSizeIs("Doe, Jo*", 1);
        }

        @Test
        public void caseInsensitive() {
            assertThatFindPartiesSizeIs("*OE, jO*", 1);
        }

        private void assertThatFindPartiesSizeIs(String referenceOrName, int value) {
            Assert.assertThat(parties.findParties(referenceOrName).size(), is(value));
        }


    }

    public static class FindPartyByReference extends PartiesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonForJohnDoeNl());
                    executionContext.executeChild(this, new OrganisationForHelloWorldNl());
                    executionContext.executeChild(this, new OrganisationForTopModelGb());
                }
            });
        }

        @Test
        public void happyCase() throws Exception {
            Party party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
            assertThat(party, is(notNullValue()));
        }

        @Test
        public void canNotBeFound() throws Exception {
            final Party party = parties.matchPartyByReferenceOrName("HELLO");
            assertThat(party, is(nullValue()));
        }

    }

    public static class MatchPartyByReferenceOrName extends PartiesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new OrganisationForHelloWorldNl());
                }
            });
        }

        @Test
        public void happyCase() throws Exception {
            Assert.assertNotNull(parties.matchPartyByReferenceOrName(OrganisationForHelloWorldNl.REF));
        }

        @Test
        public void canNotBeFound() throws Exception {
            Assert.assertNull(parties.matchPartyByReferenceOrName("HELLO"));
        }

    }

}