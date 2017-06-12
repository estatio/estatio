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

import javax.inject.Inject;

import org.estatio.dom.agreement.role.AgreementRoleTypeRepository;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeConstants;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioOperationalTeardownFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForDagoBankGb;

import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

public class GuaranteeForOxfTopModel001Gb extends GuaranteeAbstract {

    public static final String LEASE_REFERENCE = LeaseForOxfTopModel001Gb.REF;
    public static final String REFERENCE = LEASE_REFERENCE + "-D";
    public static final String PARTY_REF_BANK = OrganisationForDagoBankGb.REF;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new EstatioOperationalTeardownFixture());
        executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
        executionContext.executeChild(this, new OrganisationForDagoBankGb());

        createGuaranteeForOxfTopModel001(executionContext);
    }

    private void createGuaranteeForOxfTopModel001(final ExecutionContext executionContext) {

        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REFERENCE);

        final Guarantee guarantee = newGuarantee(
                lease,
                REFERENCE,
                REFERENCE,
                GuaranteeType.BANK_GUARANTEE,
                ld(2014, 1, 1),
                ld(2015, 1, 1),
                "Description",
                bd(50000),
                executionContext);
        guarantee.createRole(
                agreementRoleTypeRepository.find(GuaranteeConstants.AgreementRoleType.BANK),
                partyRepository.findPartyByReference(PARTY_REF_BANK),
                null,
                null);
    }

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    LeaseRepository leaseRepository;
}
