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
package org.estatio.module.lease.fixtures.lease;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOxfDefault;
import org.estatio.module.lease.fixtures.LeaseItemAndTermsAbstract;
import org.estatio.module.lease.fixtures.lease.LeaseForOxfMediaX002Gb;

public class LeaseItemAndLeaseTermForTurnoverRentForOxfMediax002Gb extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = LeaseForOxfMediaX002Gb.REF;
    public static final String AT_PATH = ApplicationTenancyForGbOxfDefault.PATH;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeaseForOxfMediaX002Gb());

        // exec
        String leaseReference = LEASE_REF;
        Lease lease = leaseRepository.findLeaseByReference(leaseReference);

        createLeaseTermForTurnoverRent(
                leaseReference,
                AT_PATH,
                lease.getStartDate(), null,
                "7",
                executionContext);
    }

}
