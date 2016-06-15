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
package org.estatio.integtests.assets;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.geography.Country;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PropertyMenuTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new PropertyForOxfGb());
                executionContext.executeChild(this, new PropertyForKalNl());
            }
        });
    }

    @Inject
    PropertyRepository propertyRepository;

    public static class AllPropertyMenu extends PropertyMenuTest {

        @Test
        public void whenReturnsInstance_thenCanTraverseUnits() throws Exception {
            // when
            final List<Property> allProperties = propertyRepository.allProperties();

            // then
            assertThat(allProperties.size(), is(2));
        }

    }

    public static class FindPropertyMenu extends PropertyMenuTest {

        @Test
        public void withReference() throws Exception {
            final List<Property> props = propertyRepository.findProperties("OXF");
            assertNotNull(props);
            assertThat(props.size(), is(1));
        }

        @Test
        public void withName() throws Exception {
            final List<Property> props = propertyRepository.findProperties("Oxford Super Mall");
            assertNotNull(props);
            assertThat(props.size(), is(1));
        }

        @Test
        public void withWildcard() throws Exception {
            final List<Property> props = propertyRepository.findProperties("Oxford*");
            assertNotNull(props);
            assertThat(props.size(), is(1));
        }

        @Test
        public void withWildcard_returningMultiple() throws Exception {
            final List<Property> props = propertyRepository.findProperties("*");
            assertNotNull(props);
            assertThat(props.size(), is(2));
        }
    }

    public static class FindPropertyByReference extends PropertyMenuTest {

        @Test
        public void withReference() throws Exception {

            // when
            final Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

            // then
            Assertions.assertThat(property.getReference()).isEqualTo(PropertyForOxfGb.REF);
        }
    }

    public static class NewProperty extends PropertyMenuTest {

        @Inject
        private CountryRepository countryRepository;

        @Test
        public void happyCase() throws Exception {

            // given
            final Country gbrCountry = countryRepository.findCountry(CountriesRefData.GBR);

            // when
            final Property property = propertyRepository.newProperty("ARN", "Arndale", PropertyType.RETAIL_PARK, "Manchester", gbrCountry, new LocalDate(2014,4,1));

            // then
            Assertions.assertThat(property.getName()).isEqualTo("Arndale");
            Assertions.assertThat(property.getType()).isEqualTo(PropertyType.RETAIL_PARK);
            Assertions.assertThat(property.getCountry()).isEqualTo(gbrCountry);
            Assertions.assertThat(property.getCity()).isEqualTo("Manchester");
            Assertions.assertThat(property.getAcquireDate()).isEqualTo(new LocalDate(2014, 4, 1));
            Assertions.assertThat(property.getApplicationTenancy().getPath()).isEqualTo("/" + CountriesRefData.GBR + "/ARN");

        }
    }
}