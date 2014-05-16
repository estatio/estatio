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

import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class PartiesTest_findPartyByReference extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    private Parties parties;

    @Before
    public void setUp() throws Exception {
        parties = service(Parties.class);
    }
    
    @Test
    public void happyCase() throws Exception {
        Party party = parties.findPartyByReference("TOPMODEL");
        assertThat(party, is(notNullValue()));
    }

    @Test
    public void canNotBeFound() throws Exception {
        final Party party = parties.matchPartyByReferenceOrName("HELLO");
        assertThat(party, is(nullValue()));
    }

}
