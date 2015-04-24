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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class LeaseItemTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
    }

    @Inject
    Leases leases;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
    }


    public static class FindTerm extends LeaseItemTest {


        @Test
        public void whenExists_forRent() throws Exception {

            // this is mostly just asserting on the baseline fixture

            // given
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));

            // and given
            final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();
            Assert.assertThat(terms.size(), is(1));
            final LeaseTerm term0 = terms.first();

            // when
            final LeaseTerm term = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
            LeaseTermForIndexable leaseTopModelRentTerm = assertType(term, LeaseTermForIndexable.class);

            // then
            Assert.assertNotNull(leaseTopModelRentTerm);
            assertThat(leaseTopModelRentTerm, is(term0));

            // and then
            Assert.assertNotNull(leaseTopModelRentTerm.getFrequency());
            Assert.assertNotNull(leaseTopModelRentTerm.getFrequency().nextDate(VT.ld(2012, 1, 1)));

            BigDecimal baseValue = leaseTopModelRentTerm.getBaseValue();
            Assert.assertEquals(VT.bd("20000.00"), baseValue);
        }

        @Test
        public void whenExists_forServiceCharge() throws Exception {

            // given
            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

            // when
            LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.findTerm(VT.ld(2010, 7, 15));

            // then
            assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue(), Is.is(VT.bd("6000.00")));
        }


    }


    public static class Copy extends LeaseItemTest {

        @Inject
        private Charges charges;

        @Test
        public void happyCase() throws Exception {

            // given
            LeaseItem leaseItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            final Charge charge = charges.findByReference(ChargeRefData.GB_SERVICE_CHARGE);

            // when
            final LocalDate startDate = VT.ld(2011, 7, 15);
            final LeaseItem newLeaseItem = wrap(leaseItem).copy(startDate, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, charge);

            // then
            Assertions.assertThat(newLeaseItem.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            Assertions.assertThat(newLeaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            Assertions.assertThat(newLeaseItem.getStartDate()).isEqualTo(startDate);
            Assertions.assertThat(newLeaseItem.getCharge()).isEqualTo(charge);

            final BigInteger nextSequenceNumber = leaseItem.getSequence().add(VT.bi(1));
            Assertions.assertThat(newLeaseItem.getSequence()).isEqualTo(nextSequenceNumber);

            final String atPath = leaseItem.getApplicationTenancyPath();
            Assertions.assertThat(newLeaseItem.getApplicationTenancyPath()).isEqualTo(atPath);

            Assertions.assertThat(newLeaseItem.getTerms().size()).isEqualTo(leaseItem.getTerms().size());
            Assertions.assertThat(newLeaseItem.getEndDate()).isNull();
        }

    }

    public static class ChangeCharge extends LeaseItemTest {

        @Inject
        private Charges charges;

        @Test
        public void happyCase() throws Exception {

            // given
            LeaseItem leaseItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            final Charge charge = charges.findByReference(ChargeRefData.IT_SERVICE_CHARGE);
            Assertions.assertThat(leaseItem.getCharge()).isEqualTo(charge);

            // when
            final Charge newCharge = charges.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            final LeaseItem leaseItemReturned = wrap(leaseItem).changeCharge(newCharge);

            // then
            Assertions.assertThat(leaseItem.getCharge()).isEqualTo(newCharge);
            Assertions.assertThat(leaseItemReturned).isSameAs(leaseItem);
        }
    }


    public static class GetTerms extends LeaseItemTest {

        @Test
        public void whenExists_andFirstIsIndexableRent() throws Exception {
            // this is just really asserting on the fixture

            // given
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));

            // when
            final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();

            // then
            Assert.assertThat(terms.size(), is(1));
            final LeaseTerm term0 = terms.first();

            LeaseTermForIndexable indexableRent = assertType(term0, LeaseTermForIndexable.class);

            Assert.assertNotNull(indexableRent.getFrequency());
            Assert.assertNotNull(indexableRent.getFrequency().nextDate(VT.ld(2012, 1, 1)));

            BigDecimal baseValue = indexableRent.getBaseValue();
            Assert.assertEquals(VT.bd("20000.00"), baseValue);
        }

        @Test
        public void whenExists_andFirstIsLeaseTermForServiceChargeBudgetAuditLineItem() throws Exception {
            // this is just really asserting on the fixture

            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

            final SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
            Assert.assertThat(terms.size(), Is.is(1));
            final LeaseTerm term0 = terms.first();

            LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = assertType(term0, LeaseTermForServiceCharge.class);
            Assert.assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue(), Is.is(VT.bd("6000.00")));
        }

    }

    public static class Verify extends LeaseItemTest {


        private LeaseItem leaseTopModelServiceChargeItem;
        private LeaseItem leaseTopModelRentItem;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            assertNotNull(leaseTopModelServiceChargeItem);

            leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            assertNotNull(leaseTopModelRentItem);
        }

        @Test
        public void givenServiceChargeItem_thenCreatesTermsForThatItemOnly() throws Exception {

            // given
            assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
            assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));

            // when
            leaseTopModelServiceChargeItem.verify();

            // then
            assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
            assertNotNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
        }

        @Test
        public void givenIndexableRentItem_thenCreatesTermsForThatItemOnly() throws Exception {

            // given
            assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
            assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));

            // when
            leaseTopModelRentItem.verify();

            // then
            assertNotNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
            assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
        }
    }

}