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
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxfDefault;

import static org.estatio.integtests.VT.bd;

public class LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb extends LeaseItemAndTermsAbstract {

    public static final String AT_PATH = ApplicationTenancyForGbOxfDefault.PATH;
    public static final String LEASE_REF = LeaseForOxfMiracl005Gb.REF;

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

        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);
        createLeaseTermForDiscount(
                LEASE_REF,
                AT_PATH,
                lease.getStartDate(), lease.getStartDate().plusYears(1),
                bd(-20000),
                executionContext);
    }

}
