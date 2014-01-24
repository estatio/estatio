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
package org.estatio.integration.tests.lease;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class LeaseTest_assign extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }
    
    private Leases leases;
 
    private Lease leasePoison;
    private Lease leaseMediax;
    
    @Before
    public void setup() {
        leases = service(Leases.class);
        leasePoison = leases.findLeaseByReference("OXF-POISON-003");
        leaseMediax = leases.findLeaseByReference("OXF-MEDIAX-002");
    }
    
    @Test
    public void happyCase() throws Exception {
        Lease newLease = leasePoison.assign("OXF-MEDIAX-003" , "Reassigned", leaseMediax.getSecondaryParty() , new LocalDate(2014,1,1), new LocalDate(2014,1,1), true);
    
    }

}
