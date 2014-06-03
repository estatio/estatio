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
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class LeasesTest_findLeaseByReference extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new LeaseForOxfTopModel001(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    @Test
    public void whenValidReference() {
        final Lease lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
        Assert.assertEquals(LeaseForOxfTopModel001.LEASE_REFERENCE, lease.getReference());
    }


}
