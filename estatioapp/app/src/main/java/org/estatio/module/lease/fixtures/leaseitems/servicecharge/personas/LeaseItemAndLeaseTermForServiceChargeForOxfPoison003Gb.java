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
package org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.fixtures.LeaseItemAndTermsAbstract;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfPoison003Gb;

import static org.incode.module.base.integtests.VT.bd;

public class LeaseItemAndLeaseTermForServiceChargeForOxfPoison003Gb extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = Lease_enum.OxfPoison003Gb.getRef();

    public static final String AT_PATH = ApplicationTenancy_enum.GbOxfDefault.getPath();

    @Override
    protected void execute(final ExecutionContext fixtureResults) {
        createLeaseTermsForOxfPoison003(fixtureResults);
    }

    private void createLeaseTermsForOxfPoison003(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeaseForOxfPoison003Gb());

        // exec
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        createLeaseTermForServiceCharge(
                LEASE_REF,
                AT_PATH,
                lease.getStartDate(), null,
                bd(12400),
                executionContext,
                LeaseAgreementRoleTypeEnum.LANDLORD);
    }

}
