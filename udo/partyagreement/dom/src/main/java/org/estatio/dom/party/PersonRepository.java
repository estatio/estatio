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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Dflt;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Person.class)
public class PersonRepository extends UdoDomainRepositoryAndFactory<Person> {

    public PersonRepository() {
        super(PersonRepository.class, Person.class);
    }

    // //////////////////////////////////////

    public Person newPerson(
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = Person.ReferenceType.Meta.REGEX, regexPatternReplacement = Person.ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = Person.InitialsType.Meta.REGEX, regexPatternReplacement = Person.InitialsType.Meta.REGEX_DESCRIPTION) String initials,
            final @Parameter(optionality = Optionality.OPTIONAL) String firstName,
            final String lastName,
            final PersonGenderType gender,
            final ApplicationTenancy applicationTenancy) {
        final Person person = newTransientInstance(Person.class);
        person.setApplicationTenancyPath(applicationTenancy.getPath());
        person.setReference(reference);
        person.change(gender, initials, firstName, lastName);
        persist(person);
        return person;
    }

    @Programmatic
    public List<ApplicationTenancy> choices5NewPerson() {
        return estatioApplicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    @Programmatic
    public ApplicationTenancy default5NewPerson() {
        return Dflt.of(choices5NewPerson());
    }

    public String validateNewPerson(
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final ApplicationTenancy applicationTenancy
    ) {
        return partyRepository.validateNewParty(reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Person> allPersons() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    private PartyRepository partyRepository;

}
