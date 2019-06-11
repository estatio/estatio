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
package org.estatio.module.numerator.dom;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

/**
 * Acts as an adapter between the pure NumeratorRepository and the legacy clients.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorAtPathRepository  {

    /**
     * This can either be global, or per country
     *
     * @param numeratorName
     * @param applicationTenancyIfAny - determines the country, or might be <tt>null</tt>.
     *
     * @return
     */
    public Numerator findGlobalNumerator(
            final String numeratorName,
            final ApplicationTenancy applicationTenancyIfAny) {
        final Country countryIfAny = toCountry(applicationTenancyIfAny);
        return numeratorRepository.find(numeratorName, countryIfAny, null, null);
    }

    public Numerator findNumerator(
            final String numeratorName,
            final Object scopedToIfAny,
            final ApplicationTenancy applicationTenancy) {
        final Country countryIfAny = toCountry(applicationTenancy);
        return numeratorRepository.find(numeratorName, countryIfAny, scopedToIfAny, null);
    }

    public Numerator createGlobalNumerator(
            final String numeratorName,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        final Country countryIfAny = toCountry(applicationTenancy);
        return numeratorRepository.create(
                    numeratorName, countryIfAny, null, null, format, lastIncrement, applicationTenancy);
    }

    public Numerator findOrCreateNumerator(
            final String numeratorName,
            final Object scopedToIfAny,
            final String format,
            final BigInteger initialValue,
            final ApplicationTenancy applicationTenancy) {
        final Country countryIfAny = toCountry(applicationTenancy);
        return numeratorRepository.findOrCreate(numeratorName, countryIfAny, scopedToIfAny, null, format, initialValue, applicationTenancy);
    }

    public Numerator createScopedNumerator(
            final String numeratorName,
            final Object scopedTo,
            final String formatStr,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        final Country countryIfAny = toCountry(applicationTenancy);
        return numeratorRepository.create(numeratorName, countryIfAny, scopedTo, null, formatStr, lastIncrement, applicationTenancy);
    }

    private Country toCountry(final ApplicationTenancy applicationTenancyIfAny) {
        final String atPath = applicationTenancyIfAny != null ? applicationTenancyIfAny.getPath() : null;
        return countryRepository.findCountryByAtPath(atPath);
    }

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    CountryRepository countryRepository;

}
