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
package org.estatio.integtests.lease;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.assertj.core.api.Assertions;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.*;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LeaseItemsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
            }
        });
    }

    @Inject
    Leases leases;

    @Inject
    LeaseItems leaseItems;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = leases.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
    }

    public static class FindLeaseItem extends LeaseItemsTest {

        @Test
        public void findLeaseItem() throws Exception {
            LeaseItem leaseItem = leaseItems.findLeaseItem(lease, LeaseItemType.RENT, lease.getStartDate(), BigInteger.valueOf(1));
            assertTrue(lease.getItems().contains(leaseItem));
        }
    }

    public static class FindLeaseItemByType extends LeaseItemsTest {

        @Test
        public void findLeaseItemByType() throws Exception {
            // given
            LeaseItem currentItem = leaseItems.findLeaseItem(lease, LeaseItemType.RENT, lease.getStartDate(), BigInteger.valueOf(1));

            // when
            final ApplicationTenancy firstLocalAppTenancy = lease.getApplicationTenancy().getChildren().first();
            LeaseItem newItem = leaseItems.newLeaseItem(lease, currentItem.getType(), currentItem.getCharge(), currentItem.getInvoicingFrequency(), currentItem.getPaymentMethod(), currentItem.getStartDate().plusYears(1));
            lease.getItems().add(newItem);

            // then
            List<LeaseItem> results = leaseItems.findLeaseItemsByType(lease, LeaseItemType.RENT);
            assertThat(results.size(), is(2));
        }
    }

    public static class NewItem extends LeaseItemsTest {

        private Lease leasePoison;

        @Before
        public void setup() {
            leasePoison = leases.findLeaseByReference(LeaseForOxfPoison003Gb.REF);
        }


        @Inject
        private ChargeRepository chargeRepository;
        @Inject
        private WrapperFactory wrapperFactory;

        @Test
        public void happyCase() throws Exception {

            // given
            final Charge charge = chargeRepository.findByReference(ChargeRefData.GB_DISCOUNT);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();
            final ApplicationTenancy firstChildAppTenancy = leaseAppTenancy.getChildren().first();

            // when
            final LeaseItem leaseItem = wrap(leaseItems).newLeaseItem(
                    leasePoison,
                    LeaseItemType.DISCOUNT,
                    charge,
                    InvoicingFrequency.FIXED_IN_ADVANCE,
                    PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate()
            );

            // then
            Assertions.assertThat(leaseItem.getLease()).isEqualTo(leasePoison);
            Assertions.assertThat(leaseItem.getType()).isEqualTo(LeaseItemType.DISCOUNT);
            Assertions.assertThat(leaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            Assertions.assertThat(leaseItem.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            Assertions.assertThat(leaseItem.getStartDate()).isEqualTo(leasePoison.getStartDate());
            Assertions.assertThat(leaseItem.getSequence()).isEqualTo(VT.bi(1));
            Assertions.assertThat(leaseItem.getApplicationTenancy()).isEqualTo(firstChildAppTenancy);
        }

        @Test
        public void invalidCharge() throws Exception {

            // given
            final Charge charge = chargeRepository.findByReference(ChargeRefData.IT_DISCOUNT);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();

            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage(containsString("not valid for this lease"));

            // when
            wrap(leaseItems).newLeaseItem(
                    leasePoison, LeaseItemType.DISCOUNT, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate());
        }

    }


}
