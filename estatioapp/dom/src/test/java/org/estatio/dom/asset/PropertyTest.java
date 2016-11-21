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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.ownership.FixedAssetOwnership;
import org.incode.module.country.dom.impl.Country;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Country.class))
                    .withFixture(pojos(Location.class))
                    .withFixture(pojos(ApplicationTenancy.class))
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

    public static class NewUnit extends PropertyTest {

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

    public static class ValidateAddOwner extends PropertyTest {

        private Property property;
        private FixedAssetOwnership fixedAssetOwnership;
        private Party party;

        @Before
        public void setUp() throws Exception {
            party = new PartyForTesting();
            property = new Property();

            fixedAssetOwnership = new FixedAssetOwnership();
            fixedAssetOwnership.setOwner(party);
            fixedAssetOwnership.setFixedAsset(property);

            property.getOwners().add(fixedAssetOwnership);
            assertThat(property.getOwners()).hasSize(1);
        }

        @Test
        public void alreadyDefined() throws Exception {
            // given
            fixedAssetOwnership.setOwnershipType(OwnershipType.FULL);

            // when
            final String validation = property.validateAddOwner(party, OwnershipType.FULL);

            // then
            assertThat(validation).isEqualToIgnoringCase("This owner already has its share defined");
        }

        @Test
        public void fullOwnershipUnavailable() throws Exception {
            // given
            fixedAssetOwnership.setOwnershipType(OwnershipType.SHARED);
            final Party newParty = new PartyForTesting();

            // when
            final String validation = property.validateAddOwner(newParty, OwnershipType.FULL);

            // then
            assertThat(validation).isEqualToIgnoringCase("This owner can not be a full owner as there is already a shared owner defined");
        }
    }

    public static class Dispose extends PropertyTest {
        private Property property;

        @Test
        public void xxx() {
            // given
            property = new Property();
            // then
            assertThat(property.disableDispose(null)).isNull();
            // when
            property.dispose(new LocalDate(2000, 1, 1));
            // then
            assertThat(property.disableDispose(null)).isNotNull();
        }
    }

    public static class Ordering extends PropertyTest {

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