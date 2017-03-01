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
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Dflt;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.country.CountryServiceForCurrentUser;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.PartyConstants;
import org.estatio.dom.party.PartyRepository;
import org.estatio.numerator.dom.impl.NumeratorRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.party.OrganisationMenu"
)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.2"
)
public class OrganisationMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Organisation newOrganisation(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
            final boolean useNumereratorForReference,
            final String name,
            final Country country) {
        return organisationRepository.newOrganisation(reference, useNumereratorForReference, name, country);
    }

    public List<Country> choices3NewOrganisation() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public Country default3NewOrganisation() {
        return  Dflt.of(choices3NewOrganisation());
    }

    public String validateNewOrganisation(
            final String reference,
            final boolean useNumeratorForReference,
            final String name,
            final Country country
    ) {
        if (useNumeratorForReference) {

            final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);
            if (numeratorRepository
                    .findGlobalNumerator(PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME, applicationTenancy) == null) {
                return "No numerator found";
            }
            return null;
        }
        return partyRepository.validateNewParty(reference);
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Organisation> allOrganisations() {
        return organisationRepository.allOrganisations();
    }

    // //////////////////////////////////////

    @Inject
    OrganisationRepository organisationRepository;

    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    PartyRepository partyRepository;

}
