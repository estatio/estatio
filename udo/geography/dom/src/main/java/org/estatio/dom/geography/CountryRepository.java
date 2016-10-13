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
package org.estatio.dom.geography;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.types.ReferenceType;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Country.class)
public class CountryRepository extends UdoDomainRepositoryAndFactory<Country> {

    public CountryRepository() {
        super(CountryRepository.class, Country.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Country> newCountry(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final String alpha2Code,
            final String name) {
        createCountry(reference, alpha2Code, name);
        return allCountries();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Country> allCountries() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Country createCountry(
            final String reference,
            final String alpha2Code,
            final String name) {
        final Country country = newTransientInstance();
        country.setReference(reference);
        country.setAlpha2Code(alpha2Code);
        country.setName(name);
        persist(country);
        return country;
    }

    @Programmatic
    public Country findOrCreateCountry(
            final String reference,
            final String alpha2Code,
            final String name) {
        Country country = findCountry(reference);
        return country == null ? createCountry(reference, alpha2Code, name) : country;
    }

    @Programmatic
    public Country findCountry(
            final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<Country> countriesFor(final Iterable<String> countryCodes) {
        List<Country> available = Lists.newArrayList();
        final ImmutableMap<String, Country> countryByCode = Maps.uniqueIndex(allCountries(), new Function<Country, String>() {
            @Override
            public String apply(final Country input) {
                return input.getName();
            }
        });
        for (String countryCodeForUser : countryCodes) {
            available.add(countryByCode.get(countryCodeForUser));
        }
        return available;
    }

}
