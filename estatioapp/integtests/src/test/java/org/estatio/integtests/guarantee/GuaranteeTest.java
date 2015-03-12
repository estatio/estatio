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
package org.estatio.integtests.guarantee;

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.guarantee.Guarantees;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GuaranteeTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    @Inject
    Guarantees guarantees;

    Lease lease;

    Guarantee guaranteeWithFinancialAccount;

    Guarantee guaranteeWithoutFinancialAccount;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
            }
        }.withTracing());

    }

    @Before
    public void setUp() {
        lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
        guaranteeWithFinancialAccount = guarantees.findByReference(_LeaseForOxfTopModel001Gb.REF + "-D");
        GuaranteeType guaranteeType = GuaranteeType.UNKNOWN;
        guaranteeWithoutFinancialAccount = guarantees.newGuarantee(
                lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, VT.ld("20120101"), null, "", VT.bd(1000), null);
    }

    public static class ChangeGuaranteeType extends GuaranteeTest {

        @Test
        public void happyCase1() throws Exception {
            // when
            guaranteeWithoutFinancialAccount.changeGuaranteeType(GuaranteeType.COMPANY_GUARANTEE);

            // then
            assertThat(guaranteeWithoutFinancialAccount.getGuaranteeType(), is(GuaranteeType.COMPANY_GUARANTEE));
        }

        @Test
        public void happyCase2() throws Exception {
            // when
            guaranteeWithoutFinancialAccount.changeGuaranteeType(GuaranteeType.BANK_GUARANTEE);
            FinancialAccount financialAccount = guaranteeWithoutFinancialAccount.getFinancialAccount();
            Party secondaryParty = lease.getSecondaryParty();

            // then
            assertThat(guaranteeWithoutFinancialAccount.getGuaranteeType(), is(GuaranteeType.BANK_GUARANTEE));
            assertNotNull(financialAccount);
            assertThat(financialAccount.getReference(), is(guaranteeWithoutFinancialAccount.getReference()));
            assertThat(financialAccount.getOwner(), is(secondaryParty));
        }

        @Test
        public void sadCase() throws Exception {
            // when
            try {
                wrap(guaranteeWithFinancialAccount).changeGuaranteeType(GuaranteeType.UNKNOWN);
            } catch (DisabledException e) {
                // TODO: is this the right way to test disabledXxx() testing?
            }

            // then
            assertThat(guaranteeWithFinancialAccount.getGuaranteeType(), is(GuaranteeType.BANK_GUARANTEE));
        }
    }
}