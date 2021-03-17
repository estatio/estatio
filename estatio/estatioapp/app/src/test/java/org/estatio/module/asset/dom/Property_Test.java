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
package org.estatio.module.asset.dom;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.asset.dom.location.LocationLookupService;

import static org.assertj.core.api.Assertions.assertThat;

public class Property_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Country.class))
                    .withFixture(pojos(Location.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .withFixture(pojos(Property.class))
                    .exercise(new Property());
        }

    }

    public static class LookupLocation extends Property_Test {

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
            assertThat(property.getLocation()).isNull();

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
            assertThat(property.getLocation()).isEqualTo(location);
        }
    }

    public static class NewUnit extends Property_Test {

        @Mock
        private UnitRepository unitRepository;

        private Property property;

        @Before
        public void setup() {
            property = new Property();
            property.setReference("ABC");
            property.unitRepository = unitRepository;
        }

        @Test
        public void newUnit() {
            final String unitRef = "ABC-123";
            final String unitName = "123";
            final UnitType unitType = UnitType.CINEMA;
            context.checking(new Expectations() {
                {
                    oneOf(unitRepository).newUnit(property, unitRef, unitName, unitType);
                }
            });
            property.newUnit(unitRef, unitName, unitType);
        }

    }


    public static class Dispose extends Property_Test {
        private Property property;

        @Test
        public void xxx() {
            // given
            property = new Property();
            // then
            assertThat(property.disableDispose()).isNull();
            // when
            property.dispose(new LocalDate(2000, 1, 1));
            // then
            assertThat(property.disableDispose()).isNotNull();
        }
    }

    public static class Ordering extends Property_Test {

        private Property thisProperty;
        private Property thatProperty;

        @Before
        public void setUp() throws Exception {
            thisProperty = new Property();
            thatProperty = new Property();
        }

        @Test
        public void byDisplayOrder() {
            // given
            thisProperty.setDisplayOrder(1);
            thatProperty.setDisplayOrder(2);

            // then
            assertThat(thisProperty.compareTo(thatProperty)).isEqualTo(-1);
            assertThat(thatProperty.compareTo(thisProperty)).isEqualTo(1);
            assertThat(thisProperty.compareTo(thisProperty)).isEqualTo(0);
            assertThat(thatProperty.compareTo(thatProperty)).isEqualTo(0);
        }

    }

}