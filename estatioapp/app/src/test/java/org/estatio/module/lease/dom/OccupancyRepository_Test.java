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
package org.estatio.module.lease.dom;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class OccupancyRepository_Test {

    OccupancyRepository occupancyRepository;

    public static class occupanciesByUnitAndInterval extends OccupancyRepository_Test {

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
