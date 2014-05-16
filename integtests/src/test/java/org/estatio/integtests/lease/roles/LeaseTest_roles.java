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
package org.estatio.integtests.lease.roles;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.integtests.EstatioIntegrationTest;

public class LeaseTest_roles extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    private Lease leaseTopModel;
    
    private Leases leases;

    @Before
    public void setup() {
        leases = service(Leases.class);
        
        leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");
    }

    @Test
    public void lease_roles() throws Exception {
        assertThat(leaseTopModel.getRoles().size(), is(3));
    }

}
