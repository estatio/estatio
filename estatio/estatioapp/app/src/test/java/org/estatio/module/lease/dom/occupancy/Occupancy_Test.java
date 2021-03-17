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
package org.estatio.module.lease.dom.occupancy;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.base.dom.with.WithIntervalMutableContractTestAbstract_changeDates;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.ActivityRepository;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.UnitSize;

import static org.assertj.core.api.Assertions.assertThat;

public class Occupancy_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Occupancy occupancy;

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Lease.class))
                    .withFixture(pojos(Unit.class))
                    .withFixture(pojos(UnitSize.class))
                    .withFixture(pojos(Sector.class))
                    .withFixture(pojos(Activity.class))
                    .withFixture(pojos(Brand.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new Occupancy(),
                            PojoTester.FilterSet.excluding("unitSizeName", "sectorName", "activityName", "brandName"));
        }
    }

    public static class ChangeDates extends WithIntervalMutableContractTestAbstract_changeDates<Occupancy> {

        Occupancy occupancy;

        @Before
        public void setUp() throws Exception {
            occupancy = withIntervalMutable;
        }

        protected Occupancy doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Occupancy> mockChangeDates) {
            return new Occupancy() {
                @Override
                WithIntervalMutable.Helper<Occupancy> getChangeDates() {
                    return mockChangeDates;
                }
            };
        }

        // //////////////////////////////////////

        @Test
        public void changeDatesDelegate() {
            occupancy = new Occupancy();
            assertThat(occupancy.getChangeDates()).isNotNull();
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<Occupancy> {

        private Lease lease1;
        private Lease lease2;

        private Unit unit1;
        private Unit unit2;

        @Before
        public void setUp() throws Exception {
            lease1 = new Lease();
            lease1.setReference("ABC");

            lease2 = new Lease();
            lease2.setReference("DEF");

            unit1 = new Unit();
            unit1.setName("ABC");

            unit2 = new Unit();
            unit2.setName("DEF");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<Occupancy>> orderedTuples() {
            return listOf(
                    listOf(
                            newLeaseUnit(null, null, null),
                            newLeaseUnit(lease1, null, null),
                            newLeaseUnit(lease1, null, null),
                            newLeaseUnit(lease2, null, null))
                    , listOf(
                            newLeaseUnit(lease1, new LocalDate(2012, 4, 2), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, null, unit1))
                    , listOf(
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), null),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit2)));
        }

        private Occupancy newLeaseUnit(
                Lease lease,
                LocalDate startDate,
                Unit unit) {
            final Occupancy ib = new Occupancy();
            ib.setLease(lease);
            ib.setUnit(unit);
            ib.setStartDate(startDate);
            return ib;
        }

    }

    @Test
    public void validateNewOccupancy_Passes_Test(){
        // given
        Lease lease = new Lease();
        LocalDate occStartDate = new LocalDate();

        // when
        Unit unit = new Unit(){
            @Override
            public boolean isActiveOn(final LocalDate date) {
                return true;
            }
        };
        // then
        assertThat(lease.validateNewOccupancy(occStartDate, unit)).isNull();

    }

    @Test
    public void validateNewOccupancy_Fails_Test() {
        // given
        Lease lease = new Lease();
        LocalDate occStartDate = new LocalDate();

        // when
        Unit unit = new Unit(){
            @Override
            public boolean isActiveOn(final LocalDate date) {
                return false;
            }
        };

        // then
        assertThat(lease.validateNewOccupancy(occStartDate, unit)).isEqualTo("At the start date of the occupancy this unit is not available.");
    }

    @Mock
    ActivityRepository mockActivityRepository;

    @Test
    public void validateChangeClassification_works_when_activity_not_found_for_sector() throws Exception {

        // given
        occupancy = new Occupancy();
        occupancy.activityRepository = mockActivityRepository;
        Sector sector = new Sector();
        Activity activity = new Activity();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockActivityRepository).findBySector(sector);
        }});

        // when, then
        assertThat(occupancy.validateChangeClassification(null, sector, activity, null)).isEqualTo("Activity not found for sector");

    }

    @Test
    public void validateChangeClassification_works_when_activity_found_for_sector() throws Exception {

        // given
        occupancy = new Occupancy();
        occupancy.activityRepository = mockActivityRepository;
        Sector sector = new Sector();
        Activity activity = new Activity();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockActivityRepository).findBySector(sector);
            will(returnValue(Arrays.asList(activity)));
        }});

        // when, then
        assertThat(occupancy.validateChangeClassification(null, sector, activity, null)).isNull();

    }

    @Test
    public void effective_end_date_works() throws Exception {

        // given
        LocalDate startDate;
        LocalDate endDate;
        Occupancy occupancy = new Occupancy();
        Lease lease = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(null, null);
            }
        };
        occupancy.setLease(lease);

        // when, then
        assertThat(occupancy.getEffectiveEndDate()).isEqualTo(null);

        // when
        endDate = new LocalDate(2019,1,2);
        Lease lease2 = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(null, endDate);
            }
        };
        occupancy.setLease(lease2);
        // then
        assertThat(occupancy.getEffectiveEndDate()).isEqualTo(endDate);

        // when
        startDate = new LocalDate(2018,5,5);
        Lease lease3 = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        occupancy.setLease(lease3);
        // then
        assertThat(occupancy.getEffectiveEndDate()).isEqualTo(endDate);

        // when
        final LocalDate occEndDate = new LocalDate(2019, 1, 3);
        occupancy.setEndDate(occEndDate);
        // then
        assertThat(occupancy.getEffectiveEndDate()).isEqualTo(occEndDate);

    }

    @Test
    public void effective_start_date_works() throws Exception {

        // given
        LocalDate startDate;
        LocalDate endDate;
        Occupancy occupancy = new Occupancy();
        Lease lease = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(null, null);
            }
        };
        occupancy.setLease(lease);

        // when, then
        assertThat(occupancy.getEffectiveStartDate()).isEqualTo(null);

        // when
        startDate = new LocalDate(2018,5,5);
        Lease lease2 = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, null);
            }
        };
        occupancy.setLease(lease2);
        // then
        assertThat(occupancy.getEffectiveStartDate()).isEqualTo(startDate);

        // when
        endDate = new LocalDate(2019,1,2);
        Lease lease3 = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(startDate, endDate);
            }
        };
        occupancy.setLease(lease3);
        // then
        assertThat(occupancy.getEffectiveStartDate()).isEqualTo(startDate);

        // when
        Lease lease4 = new Lease(){
            @Override
            public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(null, endDate);
            }
        };
        occupancy.setLease(lease4);
        // then
        assertThat(occupancy.getEffectiveStartDate()).isEqualTo(null);

        // when
        final LocalDate occStartDate = new LocalDate(2019, 1, 1);
        occupancy.setStartDate(occStartDate);
        // then
        assertThat(occupancy.getEffectiveStartDate()).isEqualTo(occStartDate);

    }

}