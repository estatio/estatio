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

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountriesTest extends EstatioIntegrationTest {
    public static class FindCountryByReference extends CountriesTest {

        @Before
        public void setupData() {
            runFixtureScript(new EstatioBaseLineFixture());
        }

        @Inject
        private Countries countries;

        @Test
        public void whenExists() throws Exception {
            final Country country = countries.findCountry(CountriesRefData.NLD);
            assertThat(country.getReference(), is(CountriesRefData.NLD));
        }

    }
}