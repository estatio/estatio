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
package org.estatio.module.asset.integtests.party.relationship;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForGinoVannelliGb;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.relationship.PartyRelationshipView;
import org.estatio.module.party.dom.relationship.Party_PartyRelationshipContributions;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PartyRelationshipViewService_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Organisation_enum.TopModelGb.toFixtureScript());
                executionContext.executeChild(this, new PersonAndRolesForGinoVannelliGb());
            }
        });
        org = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        person = partyRepository.findPartyByReference(Person_enum.GinoVannelliGb.getRef());
    }

    private Party org;

    private Party person;

    @Inject
    private Party_PartyRelationshipContributions service;

    @Inject
    private PartyRepository partyRepository;

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
