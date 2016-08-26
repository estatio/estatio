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
package org.estatio.integtests.geography;

import java.util.List;

import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.StateRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StateRepositoryTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new EstatioBaseLineFixture());
    }

    @Inject
    CountryRepository countryRepository;

    @Inject
    StateRepository stateRepository;

    public static class FindStateRepositoryByCountry extends StateRepositoryTest {

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

    public static class FindState extends StateRepositoryTest {

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
