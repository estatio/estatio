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

import com.google.common.collect.Lists;

import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonGenderType;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.fixtures.person.builders.ApplicationUserBuilder;
import org.estatio.module.party.fixtures.person.builders.PersonBuilder;
import org.estatio.module.party.fixtures.person.builders.PersonPartyRolesBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class PersonAndRolesBuilder extends BuilderScriptAbstract<PersonAndRolesBuilder> {


    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String initials;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private PersonGenderType personGenderType;

    @Getter @Setter
    private String phoneNumber;

    @Getter @Setter
    private String emailAddress;

    @Getter @Setter
    private String fromPartyStr;

    @Getter @Setter
    private String relationshipType;

    @Getter @Setter
    private String securityUsername;

    @Getter @Setter
    private String securityUserAccountCloneFrom;

    @Getter
    private List<IPartyRoleType> partyRoleTypes = Lists.newArrayList();
    public PersonAndRolesBuilder addPartyRoleType(IPartyRoleType partyRoleType) {
        partyRoleTypes.add(partyRoleType);
        return this;
    }

    @Getter
    private List<PersonFixedAssetRolesBuilder.FixedAssetRoleSpec> fixedAssetRoleSpecs = Lists.newArrayList();
    public PersonAndRolesBuilder addFixedAssetRole(
            final FixedAssetRoleTypeEnum fixedAssetRoleType,
            final String propertyRef) {
        fixedAssetRoleSpecs.add(new PersonFixedAssetRolesBuilder.FixedAssetRoleSpec(fixedAssetRoleType, propertyRef));
        return this;
    }

   @Getter
    private Person person;

    @Getter
    private ApplicationUser applicationUser;

    @Getter
    PartyRelationship partyRelationship;

    @Getter
    private List<FixedAssetRole> fixedAssetRoles;

    @Getter
    private List<PartyRole> partyRoles;

    @Override
    public void execute(ExecutionContext executionContext) {

        PersonBuilder personBuilder = new PersonBuilder();
        ApplicationUserBuilder applicationUserBuilder = new ApplicationUserBuilder();
        PersonCommsBuilder personCommsBuilder = new PersonCommsBuilder();
        PersonRelationshipBuilder personRelationshipBuilder = new PersonRelationshipBuilder();

        PersonPartyRolesBuilder personPartyRolesBuilder = new PersonPartyRolesBuilder();
        PersonFixedAssetRolesBuilder fixedAssetRolesBuilder = new PersonFixedAssetRolesBuilder();

        person = personBuilder
                .setAtPath(atPath)
                .setFirstName(firstName)
                .setInitials(initials)
                .setLastName(lastName)
                .setPersonGenderType(personGenderType)
                .setReference(reference)
                .build(this, executionContext)
                .getPerson();

        if(securityUsername != null) {
            applicationUser = applicationUserBuilder
                    .setPerson(person)
                    .setSecurityUsername(securityUsername)
                    .setSecurityUserAccountCloneFrom(securityUserAccountCloneFrom)
                    .build(this, executionContext)
                    .getApplicationUser();
        }


        if(emailAddress != null || phoneNumber != null) {
            personCommsBuilder
                    .setPerson(person)
                    .setEmailAddress(emailAddress)
                    .setPhoneNumber(phoneNumber)
                    .build(this, executionContext);
        }

        if(relationshipType != null) {
            partyRelationship = personRelationshipBuilder
                    .setPerson(person)
                    .setFromPartyStr(fromPartyStr)
                    .setRelationshipType(relationshipType)
                    .build(this, executionContext)
                    .getPartyRelationship();
        }

        partyRoles = personPartyRolesBuilder
                .setPerson(person)
                .addPartyRoleTypes(partyRoleTypes)
                .build(this, executionContext)
                .getPartyRoles();

        fixedAssetRolesBuilder
                .setPerson(person)
                .addFixedAssetRoles(fixedAssetRoleSpecs)
                .build(this, executionContext);
    }
}

