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
package org.estatio.module.party.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.numerator.dom.NumeratorRepository;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Organisation.class)
public class OrganisationRepository extends UdoDomainRepositoryAndFactory<Organisation> {

    public OrganisationRepository() {
        super(OrganisationRepository.class, Organisation.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public Organisation newOrganisation(
            final String reference,
            final boolean useNumeratorForReference,
            final String name,
            final Country country) {

        final ApplicationTenancy applicationTenancy =
                estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        return newOrganisation(reference, useNumeratorForReference, name, applicationTenancy);
    }

    @Programmatic
    public Organisation findOrCreateOrganisation(
            final String reference,
            final boolean useNumeratorForReference,
            final String name,
            final ApplicationTenancy applicationTenancy) {

        final Party party = partyRepository.findPartyByReference(reference);
        if (party != null){
            if (party instanceof Organisation){
                return (Organisation) party;
            } else {
                new IllegalArgumentException("This should never happen");
            }

        }
        return newOrganisation(reference, useNumeratorForReference, name, applicationTenancy);
    }

    @Programmatic
    public Organisation newOrganisation(
            final String reference,
            final boolean useNumeratorForReference,
            final String name,
            final ApplicationTenancy applicationTenancy) {

        final Organisation organisation = newTransientInstance(Organisation.class);
        organisation.setApplicationTenancyPath(applicationTenancy.getPath());

        String refToUse = reference;
        if (useNumeratorForReference) {
            refToUse = referenceByNumerator(applicationTenancy);
        }

        organisation.setReference(refToUse);
        organisation.setName(name);
        persist(organisation);
        getContainer().flush();
        return organisation;
    }

    private String referenceByNumerator(final ApplicationTenancy applicationTenancy) {
        return numeratorRepository
                .findGlobalNumerator(PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME, applicationTenancy).nextIncrementStr();
    }

    public Organisation findByChamberOfCommerceCode(final String chamberOfCommerceCode) {
        return firstMatch("findByChamberOfCommerceCode",
                "chamberOfCommerceCode", chamberOfCommerceCode);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Organisation> allOrganisations() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    private NumeratorRepository numeratorRepository;

    @Inject
    private PartyRepository partyRepository;

}
