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
package org.estatio.fixture.lease;

import org.estatio.dom.lease.Lease;
import org.estatio.fixture.index.IndexRefData;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxfDefault;

import static org.estatio.integtests.VT.bd;
import static org.estatio.integtests.VT.ld;

public class LeaseItemAndLeaseTermForIndexableServiceChargeForOxfMiracl005Gb extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = LeaseForOxfMiracl005Gb.REF;
    public static final String AT_PATH = ApplicationTenancyForGbOxfDefault.PATH;
    public static final String INDEX_REF = IndexRefData.IT_REF;

    @Override
    protected void execute(final ExecutionContext fixtureResults) {
        createLeaseTermsForOxfMiracl005(fixtureResults);
    }

    private void createLeaseTermsForOxfMiracl005(final ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new LeaseForOxfMiracl005Gb());
        }

        // exec

        final Lease lease = leases.findLeaseByReference(LEASE_REF);
        createLeaseTermForIndexableServiceCharge(
                LEASE_REF,
                AT_PATH, lease.getStartDate(), null,
                bd(6000),
                ld(2010, 7, 1), ld(2011, 1, 1), ld(2011, 4, 1),
                INDEX_REF,
                executionContext);
    }
}
