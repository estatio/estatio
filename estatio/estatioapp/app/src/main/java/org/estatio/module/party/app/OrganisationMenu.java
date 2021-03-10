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

package org.estatio.module.party.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.base.Strings;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.role.*;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Dflt;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.application.app.MissingChamberOfCommerceCodeManager;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.party.dom.NumeratorAtPathRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.PartyRepository;

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

    @Inject
    NumeratorForOrganisationsRepository numeratorForOrganisationsRepository;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Organisation newOrganisation(
            @Parameter(
                    regexPattern = ReferenceType.Meta.REGEX,
                    regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION,
                    optionality = Optionality.OPTIONAL
            ) final String reference,
            final String name,
            final Country country,
            final @Nullable String chamberOfCommerceCode,
            final List<IPartyRoleType> partyRoleTypes) {
        boolean useNumerator = Strings.isNullOrEmpty(reference);
        final Organisation organisation = organisationRepository
                .newOrganisation(reference, useNumerator, name, chamberOfCommerceCode, country);
        for (IPartyRoleType partyRoleType : partyRoleTypes) {
            partyRoleRepository.findOrCreate(organisation, partyRoleType);
        }
        return organisation;
    }

    public List<Country> choices2NewOrganisation() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public List<PartyRoleType> choices4NewOrganisation() {
        return partyRoleTypeRepository.listAll();
    }

    public Country default2NewOrganisation() {
        return Dflt.of(choices2NewOrganisation());
    }

    public String validateNewOrganisation(
            final String reference,
            final String name,
            final Country country,
            final String chamberOfCommerceCode,
            final List<IPartyRoleType> partyRoleTypes
    ) {
        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);
        PartyRoleType tenantRole = partyRoleTypeRepository.findByKey(LeaseAgreementRoleTypeEnum.TENANT.getKey());
        if (applicationTenancy.getPath().startsWith("/BEL") && partyRoleTypes.contains(tenantRole)) {
            if (Strings.isNullOrEmpty(reference) || !reference.startsWith("BECL")) {
                return "The reference for Belgian organisations with role TENANT should have the following format: 'BECLxxxxx'";
            }
        } else {
            if (Strings.isNullOrEmpty(reference) && numeratorForOrganisationsRepository.findNumerator(applicationTenancy) == null) {
                return "No numerator found";
            } else {
                if (!Strings.isNullOrEmpty(reference) && numeratorForOrganisationsRepository.findNumerator(applicationTenancy) != null)
                    return "Reference must be left empty because a numerator is being used";
            }
        }

        if (Stream.of("/FRA", "/BEL").anyMatch(applicationTenancy.getPath()::startsWith)) {
            if (chamberOfCommerceCode == null)
                return "Chamber of Commerce code is mandatory for French and Belgian organisations";

            Optional<Organisation> orgIfAny =
                    organisationRepository.findByChamberOfCommerceCode(chamberOfCommerceCode)
                            .stream()
                            .filter(org -> org.getApplicationTenancy().equals(applicationTenancy))
                            .findFirst();

            return orgIfAny.map(organisation -> String.format("An organisation for this country and chamber of commerce code already exists: %s [%s]", organisation.getName(), organisation.getReference())).orElse(null);
        }

        return null;
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
    NumeratorAtPathRepository numeratorAtPathRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    PartyRoleRepository partyRoleRepository;

    @Inject PartyRoleTypeRepository partyRoleTypeRepository;

}
