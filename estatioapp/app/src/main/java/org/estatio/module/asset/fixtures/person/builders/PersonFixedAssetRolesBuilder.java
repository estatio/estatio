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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.fixtures.person.builders.PersonPartyRolesBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A superset of {@link PersonPartyRolesBuilder}, adding a {@link FixedAssetRole} and a corresponding {@link PartyRole}.
 */
@EqualsAndHashCode(of={"person"})
@Accessors(chain = true)
public class PersonFixedAssetRolesBuilder extends BuilderScriptAbstract<PersonFixedAssetRolesBuilder> {


    @Getter @Setter
    private Person person;

    @Data
    static class FixedAssetRoleSpec {
        final FixedAssetRoleTypeEnum roleType;
        final String propertyRef;
    }

    private List<FixedAssetRoleSpec> fixedAssetRoleSpecs = Lists.newArrayList();
    public PersonFixedAssetRolesBuilder addFixedAssetRole(
            final FixedAssetRoleTypeEnum fixedAssetRoleType,
            final String propertyRef) {
        fixedAssetRoleSpecs.add(new FixedAssetRoleSpec(fixedAssetRoleType, propertyRef));
        return this;
    }

    public PersonFixedAssetRolesBuilder addFixedAssetRoles(
            final List<FixedAssetRoleSpec> fixedAssetRoleTypes) {
        for (FixedAssetRoleSpec fixedAssetRoleType : fixedAssetRoleTypes) {
            addFixedAssetRole(fixedAssetRoleType.roleType, fixedAssetRoleType.propertyRef);
        }
        return this;
    }

    @Getter
    private List<FixedAssetRole> fixedAssetRoles = Lists.newArrayList();

    @Getter
    private List<PartyRole> partyRoles = Lists.newArrayList();

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("person", executionContext, Person.class);

        // party roles
        PersonPartyRolesBuilder personPartyRolesBuilder = new PersonPartyRolesBuilder();
        partyRoles = personPartyRolesBuilder
                .setPerson(person)
                .addPartyRoleTypes(fixedAssetRoleSpecs.stream().map(x -> x.roleType).collect(Collectors.toList()))
                .build(this, executionContext)
                .getPartyRoles();

        // fixed asset roles
        for (FixedAssetRoleSpec spec : fixedAssetRoleSpecs) {
            final Property property =
                    propertyRepository.findPropertyByReference(spec.getPropertyRef());
            final FixedAssetRole fixedAssetRole =
                    property.addRoleIfDoesNotExist(person, spec.roleType, null, null);
            fixedAssetRoles.add(fixedAssetRole);
        }
    }

    @Inject
    PropertyRepository propertyRepository;

}

