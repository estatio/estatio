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
package org.estatio.integtests.tax;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.incode.module.country.fixture.CountriesRefData;
import org.estatio.fixture.tax.TaxVatStdForAllCountries;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TaxRepositoryTest extends EstatioIntegrationTest {

    @Inject
    TaxRepository taxRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry applicationTenancyRepository;

    @Before
    public void setup() {
        runFixtureScript(new EstatioBaseLineFixture());
    }

    public static class AllTaxes extends TaxRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // Given
            final int size = countryRepository.allCountries().size();
            // When
            final List<Tax> taxList = taxRepository.allTaxes();
            // Then
            assertThat(taxList.size()).isEqualTo(size);
        }
    }

    public static class FindByReference extends TaxRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // Given, When
            final Tax tax = taxRepository.findByReference(TaxVatStdForAllCountries.NL_VATSTD);
            // Then
            assertThat(tax.getReference()).isEqualTo(TaxVatStdForAllCountries.NL_VATSTD);
        }
    }

    public static class FindByApplicationTenancy extends TaxRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // Given
            final Country country = countryRepository.findCountry(CountriesRefData.ITA);
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findOrCreateTenancyFor(country);
            // When
            final Collection<Tax> taxCollection = taxRepository.findByApplicationTenancy(applicationTenancy);
            // Then
            assertThat(taxCollection.size()).isEqualTo(1);
        }
    }

}