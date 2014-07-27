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
package org.estatio.integtests.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OccupanciesTest_finders extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute(new LeaseItemAndTermsForOxfTopModel001(), executionContext);
            }
        });
        lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
        unit = units.findUnitByReference(PropertyForOxf.unitReference("001"));
    }

    @Inject
    private Leases leases;

    @Inject
    private Units units;

    @Inject
    private Occupancies occupancies;

    private Lease lease;

    private Unit unit;

    @Test
    public void findByLease() throws Exception {
        assertThat(occupancies.findByLease(lease).size(), is(1));
    }

    @Test
    public void findByUnit() throws Exception {
        assertThat(occupancies.findByUnit(unit).size(), is(1));
    }

    @Test
    public void findByLeaseAndUnitAndStartDate() throws Exception {
        assertNotNull(occupancies.findByLeaseAndUnitAndStartDate(lease, unit, new LocalDate(2010, 7, 15)));
    }

}
