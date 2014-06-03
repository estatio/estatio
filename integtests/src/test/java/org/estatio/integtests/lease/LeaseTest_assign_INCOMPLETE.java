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

import javax.inject.Inject;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfMediaX002;
import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class LeaseTest_assign_INCOMPLETE extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new LeaseForOxfPoison003(), executionContext);
                execute(new LeaseForOxfMediaX002(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;
 
    private Lease leasePoison;
    private Lease leaseMediax;
    
    @Before
    public void setup() {
        leasePoison = leases.findLeaseByReference(LeaseForOxfPoison003.LEASE_REFERENCE);
        leaseMediax = leases.findLeaseByReference(LeaseForOxfMediaX002.LEASE_REFERENCE);
    }
    
    @Test
    public void happyCase() throws Exception {
        final String newReference = "OXF-MEDIA-003";
        Lease newLease = leasePoison.assign(
                newReference, "Reassigned",
                leaseMediax.getSecondaryParty() ,
                VT.ld(2014, 1, 1), VT.ld(2014, 1, 1), true);
    
    }
}
