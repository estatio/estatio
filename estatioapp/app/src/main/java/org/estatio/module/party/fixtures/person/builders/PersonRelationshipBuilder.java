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
package org.estatio.module.party.fixtures.person.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.relationship.PartyRelationshipRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"person", "fromParty", "relationshipType"}, callSuper = false)
@Accessors(chain = true)
public final class PersonRelationshipBuilder
        extends BuilderScriptAbstract<PartyRelationship, PersonRelationshipBuilder> {

    @Getter @Setter
    private Person person;

    @Getter @Setter
    private Party fromParty;

    @Getter @Setter
    private String relationshipType;

    @Getter
    PartyRelationship object;

    @Override
    public void execute(ExecutionContext executionContext) {

        checkParam("person", executionContext, Person.class);
        checkParam("fromParty", executionContext, String.class);
        checkParam("relationshipType", executionContext, String.class);

        // associate person
        object = partyRelationshipRepository
                .newRelationship(fromParty, person, relationshipType, null);

        executionContext.addResult(this, relationshipType, object);
    }

    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

}

