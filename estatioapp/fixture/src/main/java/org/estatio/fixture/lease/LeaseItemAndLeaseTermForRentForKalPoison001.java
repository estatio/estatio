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

import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.index.IndexRefData;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNlKalDefault;

import static org.estatio.integtests.VT.bd;

public class LeaseItemAndLeaseTermForRentForKalPoison001 extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = LeaseForKalPoison001Nl.REF;
    public static final String AT_PATH = ApplicationTenancyForNlKalDefault.PATH;

    public static final String INDEX_REF_IT = IndexRefData.IT_REF;
    public static final String CHARGE_REF_IT = ChargeRefData.IT_RENT;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new LeaseForKalPoison001Nl());
        }

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
