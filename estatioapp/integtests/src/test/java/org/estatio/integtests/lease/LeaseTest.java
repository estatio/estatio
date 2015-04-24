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

import java.util.SortedSet;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease._LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class LeaseTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    public static class Assign extends LeaseTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new _LeaseForOxfPoison003Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMediaX002Gb());
                }
            });
        }

        private Lease leasePoison;
        private Lease leaseMediax;

        @Before
        public void setup() {
            leasePoison = leases.findLeaseByReference(_LeaseForOxfPoison003Gb.REF);
            leaseMediax = leases.findLeaseByReference(_LeaseForOxfMediaX002Gb.REF);
        }

        @Test
        public void happyCase() throws Exception {
            final String newReference = "OXF-MEDIA-003";
            Lease newLease = leasePoison.assign(
                    newReference,
                    "Reassigned",
                    leaseMediax.getSecondaryParty(),
                    VT.ld(2014, 1, 1),
                    true);
        }
    }


    public static class FindItem extends LeaseTest {

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

        @Test
        public void whenExists() throws Exception {

            // given
            Lease lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            assertThat(lease.getItems().size(), is(6));

            // when
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

            // then
            Assert.assertNotNull(leaseTopModelRentItem);
            Assert.assertNotNull(leaseTopModelServiceChargeItem);
        }

    }

    public static class GetItems extends LeaseTest {

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

        @Test
        public void whenNonEmpty() throws Exception {
            Lease lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            assertThat(lease.getItems().size(), is(6));
        }
    }

    public static class NewItem extends LeaseTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new _LeaseForOxfPoison003Gb());
                }
            });
        }

        private Lease leasePoison;

        @Before
        public void setup() {
            leasePoison = leases.findLeaseByReference(_LeaseForOxfPoison003Gb.REF);
        }


        @Inject
        private Charges charges;
        @Inject
        private WrapperFactory wrapperFactory;

        @Test
        public void happyCase() throws Exception {

            // given
            final String chargeRf = ChargeRefData.GB_DISCOUNT;
            final Charge charge = charges.findByReference(chargeRf);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();
            final ApplicationTenancy firstChildAppTenancy = leaseAppTenancy.getChildren().first();

            // when
            final LeaseItem leaseItem = wrap(leasePoison).newItem(
                    LeaseItemType.DISCOUNT, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate(), firstChildAppTenancy);

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
        public void invalidAppTenancy() throws Exception {

            // given
            final Charge charge = charges.findByReference(ChargeRefData.GB_DISCOUNT);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();

            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage(containsString("not a child app tenancy of this lease"));

            // when
            wrap(leasePoison).newItem(
                    LeaseItemType.DISCOUNT, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate(), leaseAppTenancy);
        }

        @Test
        public void invalidCharge() throws Exception {

            // given
            final Charge charge = charges.findByReference(ChargeRefData.IT_DISCOUNT);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();

            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage(containsString("not valid for this lease"));

            // when
            wrap(leasePoison).newItem(
                    LeaseItemType.DISCOUNT, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate(), leaseAppTenancy);
        }
    }

    public static class VerifyUntil extends LeaseTest {

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

        private Lease leaseTopModel;
        private LeaseItem leaseTopModelRentItem;
        private LeaseItem leaseTopModelServiceChargeItem;

        @Before
        public void setUp() throws Exception {
            leaseTopModel = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);

            leaseTopModelRentItem = leaseTopModel.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            assertNotNull(leaseTopModelRentItem);

            leaseTopModelServiceChargeItem = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            assertNotNull(leaseTopModelServiceChargeItem);
        }

        /**
         * Compare to tests that verify at the
         * {@link org.estatio.dom.lease.LeaseTerm} level.
         *
         * @see LeaseTermTest_verifyUntil#givenLeaseTermForIndexableRent()
         * @see LeaseTermTest_verifyUntil#givenLeaseTermForServiceCharge()
         */
        @Test
        public void createsTermsForLeaseTermItems() throws Exception {

            // given
            assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
            assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));

            // when
            leaseTopModel.verifyUntil(VT.ld(2014, 1, 1));

            // then
            assertNotNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
            assertNotNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
        }

    }

    public static class VerifyUntil_1 extends LeaseTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMediax002Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                }
            });
        }

        @Test
        public void happyCase1() throws Exception {
            // TODO: what is the variation being tested here ?

            // given
            Lease leaseMediax = leases.findLeaseByReference(_LeaseForOxfMediaX002Gb.REF);

            LeaseItem leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2008, 1, 1), VT.bi(1));
            LeaseTerm leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(VT.ld(2008, 1, 1));
            assertNotNull(leaseMediaXServiceChargeTerm);

            // when
            leaseMediax.verifyUntil(VT.ld(2014, 1, 1));

            // commit to get the BigDecimals to be stored to the correct
            // precision by DN.
            nextTransaction();

            // and reload
            leaseMediax = leases.findLeaseByReference("OXF-MEDIAX-002");
            leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2008, 1, 1), VT.bi(1));

            // then
            leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(VT.ld(2008, 1, 1));
            assertNotNull(leaseMediaXServiceChargeTerm);

            final LeaseTerm leaseMediaXServiceChargeTermN = leaseMediaXServiceChargeItem.getTerms().last();
            assertThat(leaseMediaXServiceChargeTermN.getEffectiveValue(), is(VT.bd("6000.00")));
        }

        @Test
        public void happyCase2() throws Exception {
            // TODO: what is the variation being tested here ?

            // given
            Lease leasePoison = leases.findLeaseByReference("OXF-POISON-003");

            LeaseItem leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, VT.ld(2011, 1, 1), VT.bi(1));
            LeaseItem leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2011, 1, 1), VT.bi(1));
            assertNotNull(leasePoisonServiceChargeItem);

            // when
            leasePoison.verifyUntil(VT.ld(2014, 1, 1));

            // commit to get the BigDecimals to be stored to the correct
            // precision by DN; reload
            nextTransaction();
            leasePoison = leases.findLeaseByReference("OXF-POISON-003");
            leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, VT.ld(2011, 1, 1), VT.bi(1));
            leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2011, 1, 1), VT.bi(1));

            // then
            final LeaseTerm leaseTerm1 = leasePoisonServiceChargeItem.findTerm(VT.ld(2011, 1, 1));
            assertNotNull(leaseTerm1);

            final LeaseTerm leaseTerm2 = leasePoisonServiceChargeItem.getTerms().last();
            assertThat(leaseTerm2.getEffectiveValue(), is(VT.bd("12400.00")));

            // and then
            SortedSet<LeaseTerm> terms = leasePoisonRentItem.getTerms();

            assertThat(
                    leasePoisonServiceChargeItem.getEffectiveInterval().toString()
                            .concat(terms.toString()),
                    terms.size(), is(3));
            assertNotNull(leasePoisonRentItem.findTerm(VT.ld(2011, 1, 1)));
        }

    }

    public static class GetRoles extends LeaseTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                }
            });
        }

        private Lease leaseTopModel;

        @Inject
        private Leases leases;

        @Before
        public void setup() {
            leaseTopModel = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
        }

        @Test
        public void whenNonEmpty() throws Exception {
            // TODO: this seems to be merely asserting on the contents of the
            // fixture
            assertThat(leaseTopModel.getRoles().size(), is(3));
        }

    }

}