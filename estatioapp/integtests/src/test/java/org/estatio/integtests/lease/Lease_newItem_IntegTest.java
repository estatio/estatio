/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.integtests.lease;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.charge.fixtures.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.incode.module.base.integtests.VT;

import static org.assertj.core.api.Assertions.assertThat;

public class Lease_newItem_IntegTest extends EstatioIntegrationTest {

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    WrapperFactory wrapperFactory;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
            }
        });
    }

    public static class NewItem extends Lease_newItem_IntegTest {

        private Lease leasePoison;

        @Before
        public void setup() {
            leasePoison = leaseRepository.findLeaseByReference(LeaseForOxfPoison003Gb.REF);
        }

        @Test
        public void happyCase() throws Exception {

            // given
            final Charge charge = chargeRepository.findByReference(ChargeRefData.GB_DISCOUNT);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();
            final ApplicationTenancy firstChildAppTenancy = leaseAppTenancy.getChildren().first();

            // when
            final LeaseItem leaseItem = wrap(leasePoison).newItem(
                    LeaseItemType.RENT_DISCOUNT_FIXED,
                    LeaseAgreementRoleTypeEnum.LANDLORD, charge,
                    InvoicingFrequency.FIXED_IN_ADVANCE,
                    PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate()
            );

            // then
            assertThat(leaseItem.getLease()).isEqualTo(leasePoison);
            assertThat(leaseItem.getType()).isEqualTo(LeaseItemType.RENT_DISCOUNT_FIXED);
            assertThat(leaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            assertThat(leaseItem.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(leaseItem.getStartDate()).isEqualTo(leasePoison.getStartDate());
            assertThat(leaseItem.getSequence()).isEqualTo(VT.bi(1));
            assertThat(leaseItem.getApplicationTenancy()).isEqualTo(firstChildAppTenancy);
        }

        @Test
        public void invalidCharge() throws Exception {

            // given
            final Charge charge = chargeRepository.findByReference(ChargeRefData.IT_DISCOUNT);

            // then
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("not valid for this lease");

            // when
            wrap(leasePoison).newItem(LeaseItemType.RENT_DISCOUNT_FIXED, LeaseAgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, leasePoison.getStartDate());
        }
    }
}
