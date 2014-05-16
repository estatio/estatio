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
package org.estatio.integtests.lease.items;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class LeaseUnitTest_tags extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    private Leases leases;
    
    private Lease leaseTopModel;
    private Occupancy leaseUnit;

    @Before
    public void setup() {
        leases = service(Leases.class);
        
        leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");
        leaseUnit = leaseTopModel.getOccupancies().first();
    }

    @Test
    public void create() throws Exception {
        final Brand brand = leaseUnit.getBrand();
        assertThat(brand, is(not(nullValue())));
        assertThat(brand.getName(), is("Topmodel"));
    }

}
