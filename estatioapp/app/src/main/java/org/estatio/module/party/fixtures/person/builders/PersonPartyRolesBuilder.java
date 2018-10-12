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

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleTypeService;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"person", "partyRoleTypes"}, callSuper = false)
@ToString(of={"person", "partyRoleTypes"})
@Accessors(chain = true)
public final class PersonPartyRolesBuilder
        extends BuilderScriptAbstract<List<PartyRole>,PersonPartyRolesBuilder> {

    @Getter @Setter
    private Person person;

    @Getter
    private List<IPartyRoleType> partyRoleTypes = Lists.newArrayList();

    @Programmatic
    public PersonPartyRolesBuilder addPartyRoleType(IPartyRoleType partyRoleType) {
        partyRoleTypes.add(partyRoleType);
        return this;
    }
    @Programmatic
    public PersonPartyRolesBuilder addPartyRoleTypes(Collection<IPartyRoleType> partyRoleTypes) {
        this.partyRoleTypes.addAll(partyRoleTypes);
        return this;
    }

    @Getter
    private List<PartyRole> object = Lists.newArrayList();

    @Override
    public void execute(ExecutionContext executionContext) {

        checkParam("person", executionContext, Person.class);

        for (IPartyRoleType partyRoleType : partyRoleTypes) {
            final PartyRole partyRole = partyRoleTypeService.createRole(person, partyRoleType);
            object.add(partyRole);
        }
    }

    @Inject
    PartyRoleTypeService partyRoleTypeService;

}

