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

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

/**
 * Acts as an adapter between the pure NumeratorRepository and the legacy clients.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorAtPathRepository  {

    public static final Pattern PATTERN_FOR_ATPATH = Pattern.compile("/(?<countryRef>[^/]+?)/[^/]+?/(?<partyRef>.+)");

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

        final String atPath = applicationTenancy.getPath();
        final Matcher matcher = PATTERN_FOR_ATPATH.matcher(atPath);

        final Party seller;
        if(matcher.matches()) {
            final String partyRef = matcher.group("partyRef");
            seller = partyRepository.findPartyByReference(partyRef);
        } else {
            seller = null;
        }

        final Country countryIfAny = toCountry(applicationTenancy);
        final ApplicationTenancy applicationTenancyToUse = estatioApplicationTenancyRepositoryForCountry
                .findOrCreateTenancyFor(countryIfAny);

        return numeratorRepository.findOrCreate(numeratorName, countryIfAny, scopedTo, seller, formatStr, lastIncrement, applicationTenancyToUse);
    }

    private Country toCountry(final ApplicationTenancy applicationTenancyIfAny) {
        final String atPath = applicationTenancyIfAny != null ? applicationTenancyIfAny.getPath() : null;
        return countryRepository.findCountryByAtPath(atPath);
    }

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepositoryForCountry;

}
