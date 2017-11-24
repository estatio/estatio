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
package org.estatio.module.asset.fixtures.person.builders;

import javax.inject.Inject;

import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.relationship.PartyRelationshipRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"person"})
@Accessors(chain = true)
public class PersonRelationshipBuilder
        extends BuilderScriptAbstract<PersonRelationshipBuilder> {

    @Getter @Setter
    private Person person;

    @Getter @Setter
    private String fromPartyStr;

    @Getter @Setter
    private String relationshipType;

    @Getter
    PartyRelationship partyRelationship;

    @Override
    public void execute(ExecutionContext executionContext) {

        checkParam("person", executionContext, Person.class);
        checkParam("fromPartyStr", executionContext, String.class);
        checkParam("relationshipType", executionContext, String.class);

        // associate person
        Party from = partyRepository.findPartyByReference(fromPartyStr);
        partyRelationship = partyRelationshipRepository
                .newRelationship(from, person, relationshipType, null);

        executionContext.addResult(this, relationshipType, partyRelationship);
    }

    @Inject
    PartyRepository partyRepository;

    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

}

