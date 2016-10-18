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
package org.estatio.dom.party;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.ProperNameType;

import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "EstatioParty" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.party.Person")    // TODO: externalize mapping
@DomainObject(editing = Editing.DISABLED)
public class Person extends Party
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {


    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }


    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = InitialsType.Meta.MAX_LEN)
    @MemberOrder(sequence = "2")
    @Getter @Setter
    private String initials;

    // //////////////////////////////////////

    @Override
    public String disableName() {
        return "Cannot be updated directly; derived from first and last names";
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = ProperNameType.Meta.MAX_LEN)
    @Getter @Setter
    private String firstName;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = ProperNameType.Meta.MAX_LEN)
    @Getter @Setter
    private String lastName;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = PersonGenderType.Type.MAX_LEN)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private PersonGenderType gender;

    public PersonGenderType defaultGender() {
        return PersonGenderType.UNKNOWN;
    }

    // //////////////////////////////////////

    public String validate() {
        return getFirstName().isEmpty() || getInitials().isEmpty() ?
                "At least the first name or initials have to be filled in" : null;
    }

    private void updateName() {
        TitleBuffer tb = new TitleBuffer();
        setName(tb.append(getLastName()).append(",", getFirstName()).toString());
    }

    public Person change(
            final PersonGenderType gender,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = InitialsType.Meta.REGEX, regexPatternReplacement = InitialsType.Meta.REGEX_DESCRIPTION) String initials,
            final String firstName,
            final String lastName) {
        setGender(gender);
        setInitials(initials);
        setFirstName(firstName);
        setLastName(lastName);
        updateName();
        return this;
    }

    public PersonGenderType default0Change() {
        return getGender();
    }

    public String default1Change() {
        return getInitials();
    }

    public String default2Change() {
        return getFirstName();
    }

    public String default3Change() {
        return getLastName();
    }


    // //////////////////////////////////////

    public static class InitialsType {

        private InitialsType() {}

        public static class Meta {

            public static final int MAX_LEN = 3;

            public static final String REGEX = "[A-Z]+";
            public static final String REGEX_DESCRIPTION = "Only letters are allowed";

            private Meta() {}

        }

    }

    public static class ReferenceType {

        private ReferenceType() {}

        public static class Meta {

            public static final String REGEX = "[A-Z,0-9,_,-,/]+";
            public static final String REGEX_DESCRIPTION = "Only letters, numbers and 3 symbols being: \"_\" , \"-\" and \"/\" are allowed";

            private Meta() {}

        }

    }
}