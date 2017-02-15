/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.app.menus.party;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.Dflt;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.country.CountryServiceForCurrentUser;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.PersonRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.party.PersonMenu"
)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.3"
)
public class PersonMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Person newPerson(
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = Person.ReferenceType.Meta.REGEX, regexPatternReplacement = Person.ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = Person.InitialsType.Meta.REGEX, regexPatternReplacement = Person.InitialsType.Meta.REGEX_DESCRIPTION) String initials,
            final @Parameter(optionality = Optionality.OPTIONAL) String firstName,
            final String lastName,
            final PersonGenderType gender,
            final Country country) {
        return personRepository.newPerson(reference, initials, firstName, lastName, gender, country);
    }

    @Programmatic
    public List<Country> choices5NewPerson() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    @Programmatic
    public Country default5NewPerson() {
        return Dflt.of(choices5NewPerson());
    }

    public String validateNewPerson(
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final Country country
    ) {
        return partyRepository.validateNewParty(reference);
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Person> allPersons() {
        return personRepository.allPersons();
    }

    // //////////////////////////////////////

    @Inject
    PersonRepository personRepository;

    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;

    @Inject
    private PartyRepository partyRepository;

}
