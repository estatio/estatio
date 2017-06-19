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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeRepository;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Person.class)
public class PersonRepository extends UdoDomainRepositoryAndFactory<Person> {

    public PersonRepository() {
        super(PersonRepository.class, Person.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public Person newPerson(
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final Country country) {

        final ApplicationTenancy applicationTenancy =
                estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        return newPerson(reference, initials, firstName, lastName, gender, applicationTenancy);
    }

    @Programmatic
    public Person newPerson(
            final String reference,
            final String initials,
            final String firstName,
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

    // //////////////////////////////////////

    @Programmatic
    public Person findByUsername(final String username) {
        return firstMatch("findByUsername", "username", username);
    }


    @Programmatic
    public List<Person> allPersons() {
        return allInstances();
    }

    @Programmatic
    public List<Person> findByRoleTypeAndAtPath(
            final IPartyRoleType iPartyRoleType,
            final String atPath) {
        PartyRoleType partyRoleType = iPartyRoleType.findOrCreateUsing(partyRoleTypeRepository);
        return findByRoleTypeAndAtPath(partyRoleType, atPath);
    }

    @Programmatic
    public List<Person> findByRoleTypeAndAtPath(
            final PartyRoleType partyRoleType,
            final String atPath) {
        final List<Party> parties = partyRepository.findByRoleTypeAndAtPath(partyRoleType, atPath);
        return parties.stream()
                .filter(Person.class::isInstance)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    // //////////////////////////////////////

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}
