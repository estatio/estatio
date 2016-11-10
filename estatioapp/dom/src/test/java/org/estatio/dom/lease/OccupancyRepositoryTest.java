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
package org.estatio.dom.lease;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.Unit;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class OccupancyRepositoryTest {

    FinderInteraction finderInteraction;

    OccupancyRepository occupancyRepository;

    Lease lease;
    Unit unit;
    LocalDate startDate;

    @Before
    public void setup() {

        lease = new Lease();
        unit = new Unit();
        startDate = new LocalDate(2013, 4, 1);

        occupancyRepository = new OccupancyRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Occupancy> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByLeaseAndUnitAndStartDate extends OccupancyRepositoryTest {
        @Test
        public void happyCase() {

            occupancyRepository.findByLeaseAndUnitAndStartDate(lease, unit, startDate);
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Occupancy.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByLeaseAndUnitAndStartDate");
            assertThat(finderInteraction.getArgumentsByParameterName().get("lease")).isEqualTo((Object) lease);
            assertThat(finderInteraction.getArgumentsByParameterName().get("unit")).isEqualTo((Object) unit);
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate")).isEqualTo((Object) startDate);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);

        }
    }

    public static class FindByLease extends OccupancyRepositoryTest {
    }

    @Test
    public void happyCase() {
        occupancyRepository.findByLease(lease);
        assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
        assertThat(finderInteraction.getResultType()).isEqualTo(Occupancy.class);
        assertThat(finderInteraction.getQueryName()).isEqualTo("findByLease");
        assertThat(finderInteraction.getArgumentsByParameterName().get("lease")).isEqualTo((Object) lease);
        assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
    }

    public static class FindByUnit extends OccupancyRepositoryTest {

        @Test
        public void happyCase() {
            occupancyRepository.findByUnit(unit);
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Occupancy.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByUnit");
            assertThat(finderInteraction.getArgumentsByParameterName().get("unit")).isEqualTo((Object) unit);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class occupanciesByUnitAndInterval extends OccupancyRepositoryTest {

        @Before
        public void setup() {
            Occupancy occupancy2015 = new Occupancy();
            occupancy2015.setStartDate(new LocalDate(2015, 01, 01));
            occupancy2015.setEndDate(new LocalDate(2015, 12, 31));
            Occupancy occupancy2016 = new Occupancy();
            occupancy2016.setStartDate(new LocalDate(2016, 01, 01));
            occupancy2016.setEndDate(new LocalDate(2016, 12, 31));
            occupancyRepository = new OccupancyRepository() {
                @Override
                public List<Occupancy> findByUnit(final Unit unit) {
                    return Arrays.asList(occupancy2015, occupancy2016);
                }
            };
        }

        @Test
        public void happyCase() {

            //given
            Unit unit = new Unit();
            LocalDateInterval localDateInterval = new LocalDateInterval(new LocalDate(2015, 01, 01), new LocalDate(2015, 02, 01));

            //when
            List<Occupancy> foundOccupancies = occupancyRepository.occupanciesByUnitAndInterval(unit, localDateInterval);

            //then
            assertThat(foundOccupancies).hasSize(1);

            //when
            localDateInterval = new LocalDateInterval(new LocalDate(2015, 01, 01), new LocalDate(2016, 01, 01));
            foundOccupancies = occupancyRepository.occupanciesByUnitAndInterval(unit, localDateInterval);

            //then
            assertThat(foundOccupancies).hasSize(2);

            //when
            localDateInterval = new LocalDateInterval(new LocalDate(2014, 01, 01), new LocalDate(2014, 12, 31));
            foundOccupancies = occupancyRepository.occupanciesByUnitAndInterval(unit, localDateInterval);

            //then
            assertThat(foundOccupancies).hasSize(0);

            //when
            localDateInterval = new LocalDateInterval(new LocalDate(2017, 01, 01), new LocalDate(2017, 01, 01));
            foundOccupancies = occupancyRepository.occupanciesByUnitAndInterval(unit, localDateInterval);

            //then
            assertThat(foundOccupancies).hasSize(0);
        }

    }

}
