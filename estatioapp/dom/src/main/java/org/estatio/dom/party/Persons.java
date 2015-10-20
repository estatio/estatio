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

import java.util.List;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.Dflt;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

@DomainService(repositoryFor = Person.class)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.3")
public class Persons extends UdoDomainRepositoryAndFactory<Person> {

    public Persons() {
        super(Persons.class, Person.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Person newPerson(
            final @ParameterLayout(named = "reference") @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.Person.REFERENCE) String reference,
            final @ParameterLayout(named = "initials") @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.Person.INITIALS) String initials,
            final @ParameterLayout(named = "First name") @Parameter(optionality = Optionality.OPTIONAL) String firstName,
            final @ParameterLayout(named = "Last name") String lastName,
            final @ParameterLayout(named = "Gender") PersonGenderType gender,
            final ApplicationTenancy applicationTenancy) {
        final Person person = newTransientInstance(Person.class);
        person.setApplicationTenancyPath(applicationTenancy.getPath());
        person.setReference(reference);
        person.change(gender, initials, firstName, lastName);
        persist(person);
        return person;
    }


    public List<ApplicationTenancy> choices5NewPerson() {
        return estatioApplicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default5NewPerson() {
        return Dflt.of(choices5NewPerson());
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Person> allPersons() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
