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
package org.estatio.integtests.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.CollectionNumerators;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003;
import org.estatio.fixture.party.OrganisationForHelloWorld;
import org.estatio.fixture.party.OrganisationForPoison;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

public class InvoicesTest extends EstatioIntegrationTest {

    @Inject
    Invoices invoices;
    @Inject
    CollectionNumerators collectionNumerators;

    public static class CreateCollectionNumberNumerator extends InvoicesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                }
            });
        }

        @Test
        public void createThenFind() throws Exception {
            // when
            Numerator numerator = collectionNumerators.createCollectionNumberNumerator("%09d", BigInteger.TEN);
            // then
            assertThat(numerator, is(notNullValue()));
            assertThat(numerator.getName(), is(Constants.COLLECTION_NUMBER_NUMERATOR_NAME));
            assertThat(numerator.getObjectType(), is(nullValue()));
            assertThat(numerator.getObjectIdentifier(), is(nullValue()));
            assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
        }

        @Test
        public void whenNone() throws Exception {
            Numerator numerator = collectionNumerators.findCollectionNumberNumerator();
            assertThat(numerator, is(nullValue()));
        }

    }

    public static class CreateInvoiceNumberNumerator extends InvoicesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PropertyForOxf());
                    executionContext.executeChild(this, new PropertyForKal());
                }
            });
        }

        @Inject
        private Properties properties;
        @Inject
        private BookmarkService bookmarkService;

        private Property propertyOxf;
        private Property propertyKal;

        private Bookmark propertyOxfBookmark;

        @Before
        public void setUp() throws Exception {
            propertyOxf = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
            propertyKal = properties.findPropertyByReference(PropertyForKal.PROPERTY_REFERENCE);

            propertyOxfBookmark = bookmarkService.bookmarkFor(propertyOxf);
        }

        @Test
        public void whenNoneForProperty() throws Exception {

            // given
            Numerator numerator = collectionNumerators.findInvoiceNumberNumerator(propertyOxf);
            Assert.assertNull(numerator);

            // when
            numerator = collectionNumerators.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN);

            // then
            Assert.assertNotNull(numerator);
            assertThat(numerator.getName(), is(Constants.INVOICE_NUMBER_NUMERATOR_NAME));
            assertThat(numerator.getObjectType(), is(propertyOxfBookmark.getObjectType()));
            assertThat(numerator.getObjectIdentifier(), is(propertyOxfBookmark.getIdentifier()));
            assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
        }

        @Test
        public void canCreateOnePerProperty() throws Exception {

            // given
            Numerator numerator1 = collectionNumerators.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN);
            Assert.assertNotNull(numerator1);

            // when
            Numerator numerator2 = collectionNumerators.createInvoiceNumberNumerator(propertyKal, "KAL-%05d", BigInteger.ZERO);

            // then
            Assert.assertNotNull(numerator2);
            assertThat(numerator1, is(not(numerator2)));

            assertThat(numerator1.nextIncrementStr(), is("OXF-00011"));
            assertThat(numerator2.nextIncrementStr(), is("KAL-00001"));
            assertThat(numerator2.nextIncrementStr(), is("KAL-00002"));
            assertThat(numerator1.nextIncrementStr(), is("OXF-00012"));
        }

        @Test
        public void canOnlyCreateOnePerProperty_andCannotReset() throws Exception {

            // given
            Numerator numerator1 = collectionNumerators.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN);
            Assert.assertNotNull(numerator1);

            assertThat(numerator1.nextIncrementStr(), is("OXF-00011"));

            // when
            Numerator numerator2 = collectionNumerators.createInvoiceNumberNumerator(propertyOxf, "KAL-%05d", BigInteger.ZERO);

            // then
            Assert.assertNotNull(numerator2);
            assertThat(numerator1, is(sameInstance(numerator2)));

            assertThat(numerator1.nextIncrementStr(), is("OXF-00012"));
        }

    }

    public static class FindInvoiceNumberNumerator extends InvoicesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PropertyForOxf());
                }
            });
        }

        @Inject
        private Properties properties;

        private Property propertyOxf;

        @Before
        public void setUp() throws Exception {
            propertyOxf = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
        }

        @Test
        public void whenNone() throws Exception {
            // when
            Numerator numerator = collectionNumerators.findInvoiceNumberNumerator(propertyOxf);
            // then
            Assert.assertNull(numerator);
        }

    }

    public static class FindInvoices extends InvoicesTest {

        private Property propertyKal;

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        @Inject
        private Parties parties;
        @Inject
        private Leases leases;
        @Inject
        private Properties properties;

        Lease lease;

        Party buyer;

        Party seller;
        
        @Before
        public void setUp() throws Exception {
            seller = parties.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.SELLER_PARTY);
            buyer = parties.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.BUYER_PARTY);
            lease = leases.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.LEASE);

            propertyKal = properties.findPropertyByReference(PropertyForKal.PROPERTY_REFERENCE);

            Invoice invoice = invoices.findOrCreateMatchingInvoice(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT,
                    lease, InvoiceStatus.NEW,
                    InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.startDateFor(lease),
                    null);
            invoice.setRunId(runId);
            Assert.assertNotNull(invoice);
        }

        @Test
        public void byLease() {
            List<Invoice> invoiceList = invoices.findInvoices(lease);
            assertThat(invoiceList.size(), is(1));
        }

        @Test
        public void byParty() {
            List<Invoice> invoiceList = invoices.findInvoices(buyer);
            assertThat(invoiceList.size(), is(2));
        }

        @Test
        public void byPropertyAndStatus() {
            List<Invoice> invoiceList = invoices.findInvoices(propertyKal, InvoiceStatus.NEW);
            assertThat(invoiceList.size(), is(1));
        }

        @Test
        public void byStatus() {
            List<Invoice> invoiceList = invoices.findInvoices(InvoiceStatus.NEW);
            assertThat(invoiceList.size(), is(2));
        }

        @Test
        public void byPropertyDueDate() {
            List<Invoice> invoiceList = invoices.findInvoices(propertyKal, VT.ld(2012, 1, 1));
            assertThat(invoiceList.size(), is(1));
        }

        @Test
        public void byPropertyDueDateStatus() {
            List<Invoice> invoiceList = invoices.findInvoices(propertyKal, VT.ld(2012, 1, 1), InvoiceStatus.NEW);
            assertThat(invoiceList.size(), is(1));
        }

        @Test
        public void bySellerBuyerPaymentMethodLeaseInvoiceStatusDueDate() {
            Invoice invoice = invoices.findMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.startDateFor(lease));
            assertNotNull(invoice);
        }
    }

    public static class FindInvoicesByRunId extends InvoicesTest {

        private Property kalProperty;

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        @Inject
        private Parties parties;
        @Inject
        private Leases leases;

        @Before
        public void setUp() throws Exception {
            final Party seller = parties.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.SELLER_PARTY);
            final Party buyer = parties.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.BUYER_PARTY);
            final Lease lease = leases.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE);
            final LocalDate startDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);

            Invoice invoice = invoices.findOrCreateMatchingInvoice(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                    startDate, null);
            invoice.setRunId(runId);
            Assert.assertNotNull(invoice);
        }

        @Test
        public void byRunId() {
            // when
            List<Invoice> result = invoices.findInvoicesByRunId(runId);

            // then
            assertThat(result.size(), is(1));
        }

    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class FindOrCreateMatchingInvoice extends InvoicesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003());
                }
            });
        }

        @Inject
        private Parties parties;
        @Inject
        private Leases leases;

        private Party seller;
        private Party buyer;
        private Lease lease;
        private LocalDate invoiceStartDate;

        @Before
        public void setUp() throws Exception {
            seller = parties.findPartyByReference(OrganisationForHelloWorld.PARTY_REFERENCE);
            buyer = parties.findPartyByReference(OrganisationForPoison.PARTY_REFERENCE);
            lease = leases.findLeaseByReference(LeaseForOxfPoison003.LEASE_REFERENCE);

            invoiceStartDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);
        }

        @Test
        public void whenDoesNotExist() {
            // given
            Assert.assertThat(invoices.allInvoices().isEmpty(), is(true));
            // when
            Invoice invoice = invoices.findOrCreateMatchingInvoice(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                    invoiceStartDate, null);
            // then
            Assert.assertNotNull(invoice);
            Assert.assertThat(invoices.allInvoices().isEmpty(), is(false));
        }

        @Test
        public void whenExist() {
            // given
            Invoice invoice = invoices.findOrCreateMatchingInvoice(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                    invoiceStartDate, null);
            // when
            Invoice invoice2 = invoices.findOrCreateMatchingInvoice(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                    invoiceStartDate, null);
            // then
            Assert.assertThat(invoice2, is(sameInstance(invoice)));
        }

    }

}