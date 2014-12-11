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
import java.util.List;
import java.util.SortedSet;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.*;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.PersonForJohnDoe;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LeaseTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    public static class Assign extends LeaseTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfPoison003());
                    executionContext.executeChild(this, new LeaseForOxfMediaX002());
                }
            });
        }

        private Lease leasePoison;
        private Lease leaseMediax;

        @Before
        public void setup() {
            leasePoison = leases.findLeaseByReference(LeaseForOxfPoison003.LEASE_REFERENCE);
            leaseMediax = leases.findLeaseByReference(LeaseForOxfMediaX002.LEASE_REFERENCE);
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
            runScript(new FixtureScript() {
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
            Lease lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
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
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        @Test
        public void whenNonEmpty() throws Exception {
            Lease lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
            assertThat(lease.getItems().size(), is(6));
        }
    }

    public static class VerifyUntil extends LeaseTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
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
            leaseTopModel = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);

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
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMediax002());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003());
                }
            });
        }

        @Test
        public void happyCase1() throws Exception {
            // TODO: what is the variation being tested here ?

            // given
            Lease leaseMediax = leases.findLeaseByReference(LeaseForOxfMediaX002.LEASE_REFERENCE);

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
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                }
            });
        }

        private Lease leaseTopModel;

        @Inject
        private Leases leases;

        @Before
        public void setup() {
            leaseTopModel = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
        }

        @Test
        public void whenNonEmpty() throws Exception {
            // TODO: this seems to be merely asserting on the contents of the
            // fixture
            assertThat(leaseTopModel.getRoles().size(), is(3));
        }

    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class Lifecycle extends LeaseTest {

        @BeforeClass
        public static void setupTransactionalData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonForJohnDoe());

                    executionContext.executeChild(this, new PropertyForOxf());

                    executionContext.executeChild(this, new PropertyForKal());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());

                    executionContext.executeChild(this, new LeaseForOxfPret004());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005());
                }
            });
        }

        private static final LocalDate START_DATE = VT.ld(2013, 11, 7);

        @Inject
        private Leases leases;
        @Inject
        private Invoices invoices;
        @Inject
        private Indices indices;
        @Inject
        private IndexValues indexValues;
        @Inject
        private InvoiceItemsForLease invoiceItemsForLease;

        private Lease lease;
        private LeaseItem rItem;
        private LeaseItem sItem;
        private LeaseItem tItem;

        @Before
        public void setup() throws NoSuchFieldException, IllegalAccessException {
            lease = leases.findLeaseByReference("OXF-MIRACL-005");
            rItem = lease.findFirstItemOfType(LeaseItemType.RENT);
            sItem = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
            tItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
        }

        @Test
        public void step1_verify() throws Exception {
            // when
            lease.verifyUntil(VT.ld(2015, 1, 1));
            // then
            assertThat(rItem.getTerms().size(), is(2));
            assertThat(sItem.getTerms().size(), is(2));
            assertThat(tItem.getTerms().size(), is(2));

            LeaseTermForIndexable last = (LeaseTermForIndexable) rItem.getTerms().last();
            LeaseTermForIndexable first = (LeaseTermForIndexable) rItem.getTerms().first();
            assertNotNull(last.getPrevious());
            assertThat(last.getBaseValue(), is(VT.bd(150000).setScale(2)));
            assertThat(first.getStartDate(), is(VT.ld(2013, 11, 7)));
            assertThat(last.getStartDate(), is(VT.ld(2015, 1, 1)));
            assertThat(invoices.findInvoices(lease).size(), is(0));
        }

        @Test
        public void step2_calculate() throws Exception {
            assertThat("Before calculation", rItem.getTerms().size(), is(2));
            lease.calculate(
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE,
                    START_DATE,
                    VT.ld(2013, 10, 1),
                    VT.ld(2015, 4, 1));
            approveInvoicesFor(lease);
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
            assertThat(totalApprovedOrInvoicedForItem(sItem), is(VT.bd("18103.26")));
            assertThat(invoices.findInvoices(lease).size(), is(1));
        }

        @Test
        public void step3_approveInvoice() throws Exception {
            invoices.createInvoiceNumberNumerator(lease.getProperty(), "OXF-%06d", BigInteger.ZERO);
            List<Invoice> allInvoices = invoices.allInvoices();
            Invoice invoice = allInvoices.get(allInvoices.size() - 1);
            invoice.approve();
            invoice.doInvoice(VT.ld(2013, 11, 7));
            assertThat(invoice.getInvoiceNumber(), is("OXF-000001"));
            assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
        }

        @Test
        public void step4_indexation() throws Exception {
            Index index = indices.findIndex("ISTAT-FOI");
            indexValues.newIndexValue(index, VT.ld(2013, 11, 1), VT.bd(110));
            indexValues.newIndexValue(index, VT.ld(2014, 12, 1), VT.bd(115));

            nextTransaction();

            lease.verifyUntil(VT.ld(2015, 3, 31));
            LeaseTermForIndexable term = (LeaseTermForIndexable) rItem.findTerm(VT.ld(2015, 1, 1));
            assertThat(term.getIndexationPercentage(), is(VT.bd(4.5)));
            assertThat(term.getIndexedValue(), is(VT.bd("156750.00")));
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
        }

        @Test
        public void step5_normalInvoice() throws Exception {
            lease.calculate(InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, VT.ld(2015, 4, 1), VT.ld(2015, 4, 1), VT.ld(2015, 4, 1));
            approveInvoicesFor(lease);
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
        }

        @Test
        public void step6_retroInvoice() throws Exception {
            lease.calculate(InvoiceRunType.RETRO_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, VT.ld(2015, 4, 1), VT.ld(2015, 4, 1), VT.ld(2015, 4, 1));
            // (156750 - 150000) / = 1687.5 added
            approveInvoicesFor(lease);
            assertThat(invoices.findInvoices(lease).size(), is(2));
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48").add(VT.bd("1687.50"))));
        }

        @Test
        public void step7_terminate() throws Exception {
            lease.terminate(VT.ld(2014, 6, 30), true);
        }

        // //////////////////////////////////////

        private BigDecimal totalApprovedOrInvoicedForItem(LeaseItem leaseItem) {
            BigDecimal total = BigDecimal.ZERO;
            InvoiceStatus[] allowed = { InvoiceStatus.APPROVED, InvoiceStatus.INVOICED };
            for (InvoiceStatus invoiceStatus : allowed) {
                List<InvoiceItemForLease> items = invoiceItemsForLease.findByLeaseItemAndInvoiceStatus(leaseItem, invoiceStatus);
                for (InvoiceItemForLease item : items) {
                    total = total.add(item.getNetAmount());
                }
            }
            return total;
        }

        private void approveInvoicesFor(Lease lease) {
            for (Invoice invoice : invoices.findInvoices(lease)) {
                invoice.approve();
            }
        }
    }

}