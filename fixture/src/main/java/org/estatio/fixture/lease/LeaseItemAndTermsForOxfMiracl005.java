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

import org.estatio.dom.lease.*;

import static org.estatio.integtests.VT.bd;
import static org.estatio.integtests.VT.ld;

public class LeaseItemAndTermsForOxfMiracl005 extends LeaseItemAndTermsAbstract {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        createLeaseTermsForOxfMiracl005(fixtureResults);
    }

    private void createLeaseTermsForOxfMiracl005(ExecutionContext executionContext) {

        // prereqs
        execute(new LeaseForOxfMiracl005(), executionContext);

        // exec
        Lease lease = leases.findLeaseByReference(LeaseForOxfMiracl005.LEASE_REFERENCE);

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "RENT", LeaseItemType.RENT, InvoicingFrequency.QUARTERLY_IN_ADVANCE, executionContext);

        createLeaseItemIfRequiredAndLeaseTermForRent(
                lease, lease.getStartDate(), null,
                bd(150000),
                null, null, null,
                "ISTAT-FOI",
                executionContext);
        createLeaseItemIfRequiredAndLeaseTermForRent(
                lease, ld(2015, 1, 1), null,
                null,
                ld(2013, 11, 1), ld(2014, 12, 1), null,
                "ISTAT-FOI",
                executionContext);

        createLeaseItemIfRequiredAndLeaseTermForServiceCharge(
                lease,
                lease.getStartDate(), null,
                bd(12400),
                executionContext);
        createLeaseItemIfRequiredAndLeaseTermForServiceCharge(
                lease,
                ld(2014, 1, 1), null,
                bd(13000),
                executionContext);

        createLeaseItemIfRequiredAndLeaseTermForTurnoverRent(
                lease, lease.getStartDate(), null, "7",
                executionContext);

//        createLeaseTermForDiscount(
//                lease, lease.getStartDate(), null, bd(-20000),
//                executionContext);
    }

}
