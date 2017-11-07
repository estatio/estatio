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

import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxfDefault;

import static org.incode.module.base.integtests.VT.bd;

public class LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb extends LeaseItemAndTermsAbstract {

    public static final String LEASE_REF = LeaseForOxfTopModel001Gb.REF;
    public static final String AT_PATH = ApplicationTenancyForGbOxfDefault.PATH;

    @Override
    protected void execute(final ExecutionContext fixtureResults) {
        createLeaseTermsForOxfTopModel001(fixtureResults);
    }

    private void createLeaseTermsForOxfTopModel001(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());

        // exec
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        createLeaseTermForMarketing(
                LEASE_REF,
                AT_PATH,
                LeaseAgreementRoleTypeEnum.TENANTS_ASSOCIATION, lease.getStartDate(), null,
                bd(6000),
                executionContext
        );
    }
}
