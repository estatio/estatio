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
package org.estatio.integration.tests.party;

import org.estatio.dom.party.Parties;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class PartiesTest_findParties extends EstatioIntegrationTest {

    private Parties parties;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    @Before
    public void setUp() throws Exception {
        parties = service(Parties.class);
    }
    
    @Test
    public void partialReference_wildcardsBothEnds() {
        Assert.assertThat(parties.findParties("*LLOWOR*").size(), is(1));
    }

    @Test
    public void partialName_wildcardsBothEnds() {
        Assert.assertThat(parties.findParties("*ello Wor*").size(), is(1));
    }

    @Test
    public void partialName_wildcardsAtOneEndOnly() {
        Assert.assertThat(parties.findParties("Doe, Jo*").size(), is(1));
    }

    @Test
    public void caseInsensitive() {
        Assert.assertThat(parties.findParties("*OE, jO*").size(), is(1));
    }

}
