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
import org.estatio.dom.party.Parties;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;

public class PartiesTest_findParties extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PersonForJohnDoe(), executionContext);
                execute(new OrganisationForHelloWorld(), executionContext);
            }
        });
    }

    @Inject
    private Parties parties;

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
