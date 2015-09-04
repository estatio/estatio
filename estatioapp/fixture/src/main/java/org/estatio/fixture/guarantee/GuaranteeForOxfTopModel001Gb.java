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
package org.estatio.fixture.guarantee;

import static org.estatio.integtests.VT.bd;
import static org.estatio.integtests.VT.ld;

import javax.inject.Inject;

import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeConstants;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Parties;
import org.estatio.fixture.EstatioOperationalTeardownFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForDagoBankGb;

public class GuaranteeForOxfTopModel001Gb extends GuaranteeAbstract {

    public static final String LEASE_REFERENCE = LeaseForOxfTopModel001Gb.REF;
    public static final String PARTY_REF_BANK = OrganisationForDagoBankGb.REF;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new EstatioOperationalTeardownFixture());
            executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            executionContext.executeChild(this, new OrganisationForDagoBankGb());
        }

        createGuaranteeForOxfTopModel001(executionContext);
    }

    private void createGuaranteeForOxfTopModel001(final ExecutionContext executionContext) {

        final Lease lease = leases.findLeaseByReference(LEASE_REFERENCE);
        final String reference = lease.getReference() + "-D";

        final Guarantee guarantee = newGuarantee(
                lease, reference, reference, GuaranteeType.BANK_GUARANTEE,
                ld(2014, 1, 1), ld(2015, 1, 1), "Description", bd(50000),
                executionContext);
        guarantee.createRole(
                agreementRoleTypes.findByTitle(GuaranteeConstants.ART_BANK),
                parties.findPartyByReference(PARTY_REF_BANK),
                null,
                null);
    }

    @Inject
    AgreementRoleTypes agreementRoleTypes;

    @Inject
    Parties parties;
}
