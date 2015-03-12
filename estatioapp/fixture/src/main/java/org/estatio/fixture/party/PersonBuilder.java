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
package org.estatio.fixture.party;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;

public class PersonBuilder extends PersonAbstract {

    //region > reference
    private String reference;

    public String getReference() {
        return reference;
    }
    public void setReference(final String reference) {
        this.reference = reference;
    }
    //endregion

    //region > firstName
    private String firstName;
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }
    //endregion

    //region > lastName
    private String lastName;
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
    //endregion

    //region > personGenderType
    private PersonGenderType personGenderType;
    public PersonGenderType getPersonGenderType() {
        return personGenderType;
    }

    public void setPersonGenderType(final PersonGenderType personGenderType) {
        this.personGenderType = personGenderType;
    }
    //endregion

    //region > person
    private Party person;
    public Party getPerson() {
        return person;
    }
    //endregion

    @Override
    protected void execute(ExecutionContext executionContext) {

        defaultParam("reference", executionContext, faker().lorem().fixedString(6));
        defaultParam("firstName", executionContext, faker().name().firstName());
        defaultParam("lastName", executionContext, faker().name().fullName());
        defaultParam("personGenderType", executionContext, faker().collections().anEnum(PersonGenderType.class));

        final String initials = getFirstName().substring(0,1);

        person = createPerson(ApplicationTenancyForGlobal.PATH, getReference(), initials, getFirstName(), getLastName(), getPersonGenderType(), executionContext);
    }
}
