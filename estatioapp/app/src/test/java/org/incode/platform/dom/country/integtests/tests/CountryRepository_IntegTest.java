package org.incode.platform.dom.country.integtests.tests;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.country.CountryModule;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixture.CountriesRefData;
import org.incode.platform.dom.country.integtests.CountryModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountryRepository_IntegTest extends CountryModuleIntegTestAbstract {

    public static class FindCountry extends CountryRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new CountryModule().getRefDataTeardown());
            runFixtureScript(new CountriesRefData());
        }


        @Test
        public void whenExists() throws Exception {
            final Country country = countryRepository.findCountry(CountriesRefData.NLD);
            assertThat(country.getReference(), is(CountriesRefData.NLD));
        }

    }

    public static class FindCountries extends CountryRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new CountryModule().getRefDataTeardown());
            runFixtureScript(new CountriesRefData());
        }

        @Test
        public void wildcard_matching_all() throws Exception {
            final List<Country> countries = countryRepository.findCountries(".*");
            Assertions.assertThat(countries).hasSize(5);
        }

        @Test
        public void wild_card() throws Exception {
            final List<Country> countries = countryRepository.findCountries(".*A.*");
            Assertions.assertThat(countries).hasSize(2); // ITA, FRA
        }
    }

    @Inject
    CountryRepository countryRepository;
}