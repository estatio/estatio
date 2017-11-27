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

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;
import org.estatio.module.index.fixtures.IndexRefData;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNlKalDefault;
import org.estatio.module.lease.fixtures.LeaseItemAndTermsAbstract;

import static org.incode.module.base.integtests.VT.bd;

public class LeaseItemAndLeaseTermForRentForKalPoison001 extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = LeaseForKalPoison001Nl.REF;
    public static final String AT_PATH = ApplicationTenancyForNlKalDefault.PATH;

    public static final String INDEX_REF_IT = IndexRefData.IT_REF;
    public static final String CHARGE_REF_IT = ChargeRefData.IT_RENT;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeaseForKalPoison001Nl());

        // exec

        Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        final String leaseRef = LEASE_REF;
        final String leaseItemAtPath = AT_PATH;
        final String chargeRef = CHARGE_REF_IT;
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
                INDEX_REF_IT,
                executionContext);
    }

}
