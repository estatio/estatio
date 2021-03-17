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

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.fixtures.person.builders.PersonPartyRolesBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * A superset of {@link PersonPartyRolesBuilder}, adding a {@link FixedAssetRole} and a corresponding {@link PartyRole}.
 */
@EqualsAndHashCode(of={"person", "fixedAssetRoleSpecs"}, callSuper = false)
@ToString(of={"person", "fixedAssetRoleSpecs"})
@Accessors(chain = true)
public final class PersonFixedAssetRolesBuilder
        extends BuilderScriptAbstract<List<FixedAssetRole>, PersonFixedAssetRolesBuilder> {

    @Getter @Setter
    private Person person;

    @Data
    public static class FixedAssetRoleSpec {
        final FixedAssetRoleTypeEnum roleType;
        final Property property;
    }

    @Getter @Setter
    private List<FixedAssetRoleSpec> fixedAssetRoleSpecs = Lists.newArrayList();

    @Getter
    private List<FixedAssetRole> object = Lists.newArrayList();

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
                .getObject();

        // fixed asset roles
        for (FixedAssetRoleSpec spec : fixedAssetRoleSpecs) {
            final Property property = spec.getProperty();
            final FixedAssetRole fixedAssetRole =
                    property.addRoleIfDoesNotExist(person, spec.roleType, null, null);
            object.add(fixedAssetRole);
        }
    }


}

