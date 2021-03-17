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

package org.estatio.module.capex.app;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.NumeratorAtPathRepository;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Organisation;

/**
 * TODO: refactor to call {@link NumeratorRepository} directly, rather than the legacy {@link NumeratorAtPathRepository}.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorForOrdersRepository {

    public final static String NUMERATOR_NAME = "Order number";

    public Numerator findNumerator(
            final Country country,
            final Organisation organisationIfAny) {
        final ApplicationTenancy applicationTenancy = applicationTenancyRepositoryForCountry.findOrCreateTenancyFor(country);
        return numeratorAtPathRepository.findNumerator(
                NUMERATOR_NAME,
                organisationIfAny,
                applicationTenancy);
    }

    public Numerator findNumerator(final Organisation buyer) {
        ApplicationTenancy buyerApplicationTenancy = buyer.getApplicationTenancy();
        return numeratorAtPathRepository.findNumerator(
                NUMERATOR_NAME,
                buyer,
                buyerApplicationTenancy);
    }

    public Numerator findOrCreateOrderNumerator(
            final Organisation buyer,
            final String format,
            final BigInteger lastValue) {
        ApplicationTenancy buyerApplicationTenancy = buyer.getApplicationTenancy();
        return numeratorAtPathRepository.findOrCreateNumerator(
                NUMERATOR_NAME, buyer, format, lastValue, buyerApplicationTenancy);
    }



    /**
     *
     * @param atPath - instead this should be the country; perhaps derived from the buyer - this is what is used in the {@link #findNumerator(Organisation)}, above ?
     * @param buyer
     * @param format
     * @return
     */
    public Numerator findOrCreateNumerator(
            final String atPath,
            final Organisation buyer,
            final String format) {
        ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(atPath);
        return numeratorAtPathRepository.findOrCreateNumerator(
                NUMERATOR_NAME,
                buyer,
                format,
                BigInteger.ZERO,
                applicationTenancy);
    }


    @Inject
    EstatioApplicationTenancyRepositoryForCountry applicationTenancyRepositoryForCountry;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    NumeratorAtPathRepository numeratorAtPathRepository;
}
