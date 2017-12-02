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
package org.estatio.module.lease.fixtures.leaseitems.rent.personas;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.index.fixtures.enums.Index_enum;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.fixtures.LeaseItemAndTermsAbstract;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import static org.incode.module.base.integtests.VT.bd;

public class LeaseItemAndLeaseTermForRentForKalPoison001 extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = Lease_enum.KalPoison001Nl.getRef();
    public static final String AT_PATH = ApplicationTenancy_enum.NlKalDefault.getPath();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, Lease_enum.KalPoison001Nl.builder());

        // exec

        Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        final String leaseRef = LEASE_REF;
        final String leaseItemAtPath = AT_PATH;
        final String chargeRef = Charge_enum.ItRent.getRef();
        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef,
                leaseItemAtPath,
                chargeRef,
                LeaseItemType.RENT,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        createLeaseTermForIndexableRent(
                leaseRef,
                leaseItemAtPath,
                lease.getStartDate(),
                null,
                bd(150000), null, null, null,
                Index_enum.IStatFoi.getReference(),
                executionContext);
    }

}
