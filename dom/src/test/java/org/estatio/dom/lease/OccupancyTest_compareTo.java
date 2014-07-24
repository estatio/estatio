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

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.estatio.dom.asset.Unit;

public class OccupancyTest_compareTo extends ComparableContractTest_compareTo<Occupancy> {

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
