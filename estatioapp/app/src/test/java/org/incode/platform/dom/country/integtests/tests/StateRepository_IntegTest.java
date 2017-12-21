package org.incode.platform.dom.country.integtests.tests;

import java.util.List;

import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.country.dom.CountryModule;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;
import org.incode.module.country.fixture.CountriesRefData;
import org.incode.module.country.fixture.StatesRefData;
import org.incode.platform.dom.country.integtests.CountryModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StateRepository_IntegTest extends CountryModuleIntegTestAbstract  {

    @Inject
    CountryRepository countryRepository;

    @Inject
    StateRepository stateRepository;

    public static class FindStateRepositoryByCountry extends StateRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new CountryModule().getRefDataTeardown());
            runFixtureScript(new CountriesRefData());
            runFixtureScript(new StatesRefData());
        }

        @Test
        public void whenCountryWithStates() throws Exception {
            // given
            final Country country = countryRepository.findCountry("NLD");
            // when
            final List<State> statesInCountry = stateRepository.findStatesByCountry(country);
            // then
            assertThat(statesInCountry.size(), Matchers.greaterThanOrEqualTo(1));
            for (State state : statesInCountry) {
                assertThat(state.getCountry(), is(country));
            }
        }
    }

    public static class FindState extends StateRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new CountryModule().getRefDataTeardown());
            runFixtureScript(new CountriesRefData());
            runFixtureScript(new StatesRefData());
        }

        @Test
        public void findState() throws Exception {
            // given
            final Country country = countryRepository.findCountry("NLD");

            // when
            final State state = stateRepository.findState("NL-DRN");

            // then
            assertThat(state.getCountry(), is(country));
        }
    }
}
