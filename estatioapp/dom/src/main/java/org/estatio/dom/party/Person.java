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
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.util.TitleBuffer;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject(editing = Editing.DISABLED)
public class Person
        extends Party
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    private String applicationTenancyPath;

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Hidden
    public String getApplicationTenancyPath() {
        return applicationTenancyPath;
    }

    public void setApplicationTenancyPath(final String applicationTenancyPath) {
        this.applicationTenancyPath = applicationTenancyPath;
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    private String initials;

    @javax.jdo.annotations.Column(length = JdoColumnLength.Person.INITIALS)
    @MemberOrder(sequence = "2")
    public String getInitials() {
        return initials;
    }

    public void setInitials(final String initials) {
        this.initials = initials;
    }

    // //////////////////////////////////////

    @Override
    public String disableName() {
        return "Cannot be updated directly; derived from first and last names";
    }

    // //////////////////////////////////////

    private String firstName;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PROPER_NAME)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    // //////////////////////////////////////

    private String lastName;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.PROPER_NAME)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    // //////////////////////////////////////

    private PersonGenderType gender;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @MemberOrder(sequence = "1")
    public PersonGenderType getGender() {
        return gender;
    }

    public void setGender(final PersonGenderType gender) {
        this.gender = gender;
    }

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
            final @ParameterLayout(named = "Gender") PersonGenderType gender,
            final @ParameterLayout(named = "initials") @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.Person.INITIALS, regexPatternReplacement = RegexValidation.Person.INITIALS_DESCRIPTION) String initials,
            final @ParameterLayout(named = "First name") String firstName,
            final @ParameterLayout(named = "Last name") String lastName) {
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

}