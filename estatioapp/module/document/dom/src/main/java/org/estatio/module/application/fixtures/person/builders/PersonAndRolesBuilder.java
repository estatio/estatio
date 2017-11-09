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
package org.estatio.module.application.fixtures.person.builders;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.value.Password;

import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGlobal;
import org.estatio.module.base.platform.fake.EstatioFakeDataService;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonGenderType;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeService;
import org.estatio.module.party.fixtures.PersonAndRolesAbstract;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class PersonAndRolesBuilder extends PersonAndRolesAbstract {

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

    @Data
    static class FixedAssetRoleSpec {
        final FixedAssetRoleTypeEnum roleType;
        final String propertyRef;
    }

    @Getter
    private List<FixedAssetRoleSpec> fixedAssetRoles = Lists.newArrayList();
    public PersonAndRolesBuilder addFixedAssetRole(
            final FixedAssetRoleTypeEnum fixedAssetRoleType,
            final String propertyRef) {
        fixedAssetRoles.add(new FixedAssetRoleSpec(fixedAssetRoleType, propertyRef));
        return this;
    }

    @Getter
    private Person person;


    @Override
    public void execute(ExecutionContext executionContext) {

        defaultParam("atPath", executionContext, ApplicationTenancyForGlobal.PATH);
        defaultParam("reference", executionContext, fakeDataService.lorem().fixedString(6));
        defaultParam("firstName", executionContext, fakeDataService.name().firstName());
        defaultParam("lastName", executionContext, fakeDataService.name().fullName());
        defaultParam("personGenderType", executionContext, fakeDataService.collections().anEnum(PersonGenderType.class));
        defaultParam("initials", executionContext, firstName.substring(0,1));
        defaultParam("securityUserAccountCloneFrom", executionContext, "estatio-admin");

        person = createPerson(getAtPath(), getReference(), getInitials(), getFirstName(), getLastName(), getPersonGenderType(), getPhoneNumber(), getEmailAddress(), getFromPartyStr(), getRelationshipType(), executionContext);

        for (IPartyRoleType partyRoleType : partyRoleTypes) {
            partyRoleTypeService.createRole(person, partyRoleType);
        }

        for (FixedAssetRoleSpec spec : fixedAssetRoles) {
            Property property = propertyRepository.findPropertyByReference(spec.getPropertyRef());
            property.addRoleIfDoesNotExist(
                    person, spec.roleType, null, null);
            partyRoleTypeService.createRole(person, spec.roleType);
        }

        if(securityUsername != null) {
            ApplicationUser userToCloneFrom = applicationUserRepository.findByUsername(securityUserAccountCloneFrom);
            if(userToCloneFrom == null) {
                throw new IllegalArgumentException("Could not find any user with username: " + securityUserAccountCloneFrom);
            }

            ApplicationUser user = applicationUserRepository.newLocalUserBasedOn(
                    securityUsername,
                    new Password("pass"), new Password("pass"),
                    userToCloneFrom, true, null);
            user.setAtPath(getAtPath());
            person.setUsername(securityUsername);
        }
    }

    @Inject
    EstatioFakeDataService fakeDataService;

    @Inject
    PartyRoleTypeService partyRoleTypeService;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    ApplicationUserRepository applicationUserRepository;

}

