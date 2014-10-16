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
package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.geography.Country;

public class PropertyTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Country.class))
                    .withFixture(pojos(Location.class))
                    .exercise(new Property());
        }

    }

    public static class LookupLocation extends PropertyTest {

        @Mock
        private LocationLookupService mockLocationLookupService;

        private Property property;

        @Before
        public void setUp() throws Exception {
            property = new Property();
            property.locationLookupService = mockLocationLookupService;
        }

        @Test
        public void test() {
            // given
            assertThat(property.getLocation(), is(nullValue()));

            // when
            final Location location = new Location();
            context.checking(new Expectations() {
                {
                    oneOf(mockLocationLookupService).lookup("Buckingham Palace, London");
                    will(returnValue(location));
                }
            });

            property.lookupLocation("Buckingham Palace, London");

            // then
            assertThat(property.getLocation(), is(location));
        }
    }

    public static class NewUnit extends PropertyTest {

        @Mock
        private Units units;

        private Property property;

        @Before
        public void setup() {
            property = new Property();
            property.setReference("ABC");
            property.unitsRepo = units;
        }

        @Test
        public void newUnit() {
            final String unitRef = "ABC-123";
            final String unitName = "123";
            final UnitType unitType = UnitType.CINEMA;
            context.checking(new Expectations() {
                {
                    oneOf(units).newUnit(unitRef, unitName, unitType);
                }
            });
            property.newUnit(unitRef, unitName, unitType);
        }

        @Test
        public void defaults() {
            assertThat(property.default0NewUnit(), is("ABC-000"));
            assertThat(property.default1NewUnit(), is("000"));
            assertThat(property.default2NewUnit(), is(UnitType.BOUTIQUE));
        }

    }

    public static class Dispose extends PropertyTest {
        private Property property;

        @Test
        public void xxx() {
            // given
            property = new Property();
            // then
            assertNull(property.disableDispose(null, false));
            // when
            property.dispose(new LocalDate(2000, 1, 1), true);
            // then
            assertNotNull(property.disableDispose(null, false));
        }
    }

}