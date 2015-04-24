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
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.relationship.PartyRelationshipView;
import org.estatio.dom.party.relationship.PartyRelationshipViewService;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PartyrelationshipViewServiceTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForTopModelGb());
                executionContext.executeChild(this, new PersonForGinoVannelliGb());
            }
        });
        org = parties.findPartyByReference(OrganisationForTopModelGb.REF);
        person = parties.findPartyByReference(PersonForGinoVannelliGb.REF);
    }

    private Party org;

    private Party person;

    @Inject
    private PartyRelationshipViewService service;

    @Inject
    private Parties parties;

    @Test
    public void parentChild() throws Exception {
        final List<PartyRelationshipView> relationships = service.relationships(org);
        assertThat(relationships.size(), is(1));
        assertThat(relationships.get(0).getTo(), is(person));
    }

    @Test
    public void childParent() throws Exception {
        final List<PartyRelationshipView> relationships = service.relationships(person);
        assertThat(relationships.size(), is(1));
        assertThat(relationships.get(0).getTo(), is(org));
    }

}
