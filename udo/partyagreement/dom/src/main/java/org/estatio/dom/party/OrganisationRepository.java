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

import org.estatio.dom.Dflt;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.numerator.NumeratorRepository;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Organisation.class)
public class OrganisationRepository extends UdoDomainRepositoryAndFactory<Organisation> {

    public OrganisationRepository() {
        super(OrganisationRepository.class, Organisation.class);
    }

    // //////////////////////////////////////

    public Organisation newOrganisation(
            final @Parameter(regexPattern = RegexValidation.REFERENCE, regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
            final boolean useNumereratorForReference,
            final String name,
            final ApplicationTenancy applicationTenancy) {
        final Organisation organisation = newTransientInstance(Organisation.class);
        organisation.setApplicationTenancyPath(applicationTenancy.getPath());
        String refToUse = reference;
        if (useNumereratorForReference) {
            refToUse = referenceByNumerator(applicationTenancy);
        }
        organisation.setReference(refToUse);
        organisation.setName(name);
        persist(organisation);
        return organisation;
    }

    @Programmatic
    private String referenceByNumerator(final ApplicationTenancy applicationTenancy) {
        return numeratorRepository
                .findGlobalNumerator(PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME, applicationTenancy).nextIncrementStr();
    }

    @Programmatic
    public List<ApplicationTenancy> choices3NewOrganisation() {
        return estatioApplicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    @Programmatic
    public ApplicationTenancy default3NewOrganisation() {
        return Dflt.of(choices3NewOrganisation());
    }

    public String validateNewOrganisation(
            final String reference,
            final boolean useNumereratorForReference,
            final String name,
            final ApplicationTenancy applicationTenancy
    ) {
        if (useNumereratorForReference) {
            if (numeratorRepository
                    .findGlobalNumerator(PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME, applicationTenancy) == null) {
                return "No numerator found";
            }
            return null;
        }
        return partyRepository.validateNewParty(reference);
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
    private PartyRepository partyRepository;

    @Inject
    private NumeratorRepository numeratorRepository;

}
