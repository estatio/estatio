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
package org.estatio.integration.tests.geography;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.geography.Countries;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class CountriesTest_finders extends EstatioIntegrationTest {

    private Countries countries;

    @Before
    public void setUp() throws Exception {
        countries = service(Countries.class);
    }
    
    @Test
    public void findCountryByReference() throws Exception {
        assertThat(countries.findCountry("NLD").getReference(), is("NLD"));
    }

}
