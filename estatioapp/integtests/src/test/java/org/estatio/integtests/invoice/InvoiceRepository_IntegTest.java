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

import java.math.BigDecimal;
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
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.integtests.VT;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForKalNl;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.lease.app.NumeratorForCollectionMenu;
import org.estatio.module.lease.imports.InvoiceImportLine;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.invoice.dom.Constants;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.charge.fixtures.ChargeRefData;
import org.estatio.module.currency.fixtures.CurrenciesRefData;
import org.estatio.module.application.fixtures.lease.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.module.application.fixtures.lease.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.module.application.fixtures.lease.LeaseForOxfPoison003Gb;
import org.estatio.module.application.fixtures.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForPoisonGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNl;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class InvoiceRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    NumeratorForCollectionMenu estatioNumeratorRepository;

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    BookmarkService bookmarkService;

    public static class CreateCollectionNumberNumerator extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                }
            });
        }

        @Test
        public void createThenFind() throws Exception {
            // when
            Numerator numerator = estatioNumeratorRepository.createCollectionNumberNumerator("%09d", BigInteger.TEN, applicationTenancyRepository.findByPath("/"));
            // then
            assertThat(numerator, is(notNullValue()));
            assertThat(numerator.getName(), is(Constants.NumeratorName.COLLECTION_NUMBER));
            assertThat(numerator.getObjectType(), is(nullValue()));
            assertThat(numerator.getObjectIdentifier(), is(nullValue()));
            assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
        }

        @Test
        public void whenNone() throws Exception {
            Numerator numerator = estatioNumeratorRepository.findCollectionNumberNumerator();
            assertThat(numerator, is(nullValue()));
        }

    }

    public static class CreateInvoiceNumberNumerator extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
                    executionContext.executeChild(this, new PropertyAndOwnerAndManagerForKalNl());
                }
            });
        }

        private Property propertyOxf;
        private Property propertyKal;

        private Bookmark propertyOxfBookmark;

        @Before
        public void setUp() throws Exception {
            propertyOxf = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForOxfGb.REF);
            propertyKal = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForKalNl.REF);

            propertyOxfBookmark = bookmarkService.bookmarkFor(propertyOxf);
        }

        @Test
        public void whenNoneForProperty() throws Exception {

            // given
            Numerator numerator = estatioNumeratorRepository.findInvoiceNumberNumerator(propertyOxf, applicationTenancyRepository.findByPath("/"));
            Assert.assertNull(numerator);

            // when
            numerator = estatioNumeratorRepository.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN, applicationTenancyRepository.findByPath("/"));

            // then
            Assert.assertNotNull(numerator);
            assertThat(numerator.getName(), is(Constants.NumeratorName.INVOICE_NUMBER));
            assertThat(numerator.getObjectType(), is(propertyOxfBookmark.getObjectType()));
            assertThat(numerator.getObjectIdentifier(), is(propertyOxfBookmark.getIdentifier()));
            assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
        }

        @Test
        public void canCreateOnePerProperty() throws Exception {

            // given
            Numerator numerator1 = estatioNumeratorRepository.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN, applicationTenancyRepository.findByPath("/"));
            Assert.assertNotNull(numerator1);

            // when
            Numerator numerator2 = estatioNumeratorRepository.createInvoiceNumberNumerator(propertyKal, "KAL-%05d", BigInteger.ZERO, applicationTenancyRepository.findByPath("/"));

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
            Numerator numerator1 = estatioNumeratorRepository.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN, applicationTenancyRepository.findByPath("/"));
            Assert.assertNotNull(numerator1);

            assertThat(numerator1.nextIncrementStr(), is("OXF-00011"));

            // when
            Numerator numerator2 = estatioNumeratorRepository.createInvoiceNumberNumerator(propertyOxf, "KAL-%05d", BigInteger.ZERO, applicationTenancyRepository.findByPath("/"));

            // then
            Assert.assertNotNull(numerator2);
            assertThat(numerator1, is(sameInstance(numerator2)));

            assertThat(numerator1.nextIncrementStr(), is("OXF-00012"));
        }

    }

    public static class FindInvoiceNumberNumerator extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
                }
            });
        }

        private Property propertyOxf;

        @Before
        public void setUp() throws Exception {
            propertyOxf = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForOxfGb.REF);
        }

        @Test
        public void whenNone() throws Exception {
            // when
            Numerator numerator = estatioNumeratorRepository.findInvoiceNumberNumerator(propertyOxf, applicationTenancyRepository.findByPath("/"));
            // then
            Assert.assertNull(numerator);
        }

    }

    public static class FindInvoiceNumberNumeratorUsingWildCard extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
                }
            });
        }

        private Property propertyOxf;
        private ApplicationTenancy applicationTenancyForOxf;
        private ApplicationTenancy applicationTenancyWithWildCard;
        private String OXFTENANCYPATH = "/GBR/OXF/GB01";
        private String WILCARDTENANCYPATH = "/GBR/%/GB01";
        private Numerator numeratorForOxfUsingWildCard;

        @Before
        public void setUp() throws Exception {
            applicationTenancyForOxf = applicationTenancyRepository.newTenancy(OXFTENANCYPATH, OXFTENANCYPATH, null);
            applicationTenancyWithWildCard = applicationTenancyRepository.newTenancy(WILCARDTENANCYPATH, WILCARDTENANCYPATH, null);
            propertyOxf = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForOxfGb.REF);
            propertyOxf.setApplicationTenancyPath(OXFTENANCYPATH);
            numeratorForOxfUsingWildCard = numeratorRepository.createScopedNumerator(
                    Constants.NumeratorName.INVOICE_NUMBER,
                    propertyOxf,
                    propertyOxf.getReference().concat("-%04d"),
                    BigInteger.ZERO,
                    applicationTenancyWithWildCard
            );
        }

        @Test
        public void whenUsingWildCardForAppTenancyPath() throws Exception {

            // when
            Numerator numerator = estatioNumeratorRepository.findInvoiceNumberNumerator(propertyOxf, applicationTenancyRepository.findByPath(OXFTENANCYPATH));

            // then
            assertThat(numerator).isEqualTo(numeratorForOxfUsingWildCard);

        }

    }

    public static class FindInvoiceRepositoryTest extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        Property propertyKal;

        Lease lease;

        Party buyer;

        Party seller;

        ApplicationTenancy applicationTenancy;

        @Before
        public void setUp() throws Exception {
            applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancyForNl.PATH);
            seller = partyRepository.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.PARTY_REF_SELLER);
            buyer = partyRepository.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.PARTY_REF_BUYER);
            lease = leaseRepository.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.LEASE_REF);

            propertyKal = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForKalNl.REF);

            InvoiceForLease invoiceForLease = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller,
                    buyer,
                    PaymentMethod.DIRECT_DEBIT,
                    lease,
                    InvoiceStatus.NEW,
                    InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.startDateFor(lease),
                    null);
            invoiceForLease.setRunId(runId);
            Assert.assertNotNull(invoiceForLease);
        }


        public static class ByLeaseTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                List<Lease> allLeases = leaseRepository.allLeases();

                assertThat(invoiceRepository.allInvoices().size(), is(2));

                List<InvoiceForLease> invoiceList = invoiceForLeaseRepository.findByLease(lease);
                assertThat(invoiceList.size(), is(1));
            }


        }
        public static class ByPartyTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                List<Invoice> invoiceList = invoiceRepository.findByBuyer(buyer);
                assertThat(invoiceList.size(), is(1));
            }


        }
        public static class ByPropertyAndStatusTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                List<InvoiceForLease> invoiceList = invoiceForLeaseRepository.findByFixedAssetAndStatus(propertyKal, InvoiceStatus.NEW);
                assertThat(invoiceList.size(), is(1));
            }


        }
        public static class ByStatusTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                List<Invoice> invoiceList = invoiceRepository.findByStatus(InvoiceStatus.NEW);
                assertThat(invoiceList.size(), is(2));
            }


        }
        public static class ByPropertyDueDateTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                List<InvoiceForLease> invoiceList = invoiceForLeaseRepository.findByFixedAssetAndDueDate(propertyKal, VT.ld(2012, 1, 1));
                assertThat(invoiceList.size(), is(1));
            }


        }
        public static class ByPropertyDueDateStatusTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                List<InvoiceForLease> invoiceList = invoiceForLeaseRepository.findByFixedAssetAndDueDateAndStatus(propertyKal, VT.ld(2012, 1, 1), InvoiceStatus.NEW);
                assertThat(invoiceList.size(), is(1));
            }

        }
        public static class BySellerBuyerPaymentMethodLeaseInvoiceStatusDueDateTest extends FindInvoiceRepositoryTest {

            @Test
            public void happy_case() {
                InvoiceForLease invoice = invoiceForLeaseRepository.findMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001.startDateFor(lease));
                assertNotNull(invoice);
            }
        }

        public static class BySendToTest extends FindInvoiceRepositoryTest {

            @Inject
            RepositoryService repositoryService;
            CommunicationChannel communicationChannel0;
            CommunicationChannel communicationChannel1;

            @Before
            public void lookupCommChannel() throws Exception {
                final List<CommunicationChannel> communicationChannels = repositoryService
                        .allInstances(CommunicationChannel.class);
                assertThat(communicationChannels.size()).isGreaterThanOrEqualTo(2);
                communicationChannel0 = communicationChannels.get(0);
                communicationChannel1 = communicationChannels.get(1);
            }

            @Test
            public void when_none() {
                final List<Invoice> invoices = invoiceRepository.findBySendTo(communicationChannel0);
                assertThat(invoices).isEmpty();
            }

            @Test
            public void when_some() {
                // given
                final List<Invoice> invoiceList = repositoryService.allInstances(Invoice.class);
                assertThat(invoiceList.size()).isGreaterThanOrEqualTo(2);
                final Invoice invoice0 = invoiceList.get(0);
                final Invoice invoice1 = invoiceList.get(1);

                invoice0.setSendTo(communicationChannel0);
                invoice1.setSendTo(communicationChannel1);

                // when
                final List<Invoice> invoices = invoiceRepository.findBySendTo(communicationChannel0);

                // then
                assertThat(invoices).containsExactly(invoice0);
            }
        }
    }

    public static class FindInvoiceRepositoryByRunId extends InvoiceRepository_IntegTest {

        private Property kalProperty;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        @Before
        public void setUp() throws Exception {
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancyForGb.PATH);
            final Party seller = partyRepository.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.PARTY_REF_SELLER);
            final Party buyer = partyRepository.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.PARTY_REF_BUYER);
            final Lease lease = leaseRepository.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
            final LocalDate startDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);

            InvoiceForLease invoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, startDate, null);
            invoice.setRunId(runId);
            Assert.assertNotNull(invoice);
        }

        @Test
        public void byRunId() {
            // when
            List<InvoiceForLease> result = invoiceForLeaseRepository.findInvoicesByRunId(runId);

            // then
            assertThat(result.size(), is(1));
        }

    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class FindOrCreateMatchingInvoice extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                }
            });
        }

        private ApplicationTenancy applicationTenancy;
        private Party seller;
        private Party buyer;
        private Lease lease;
        private LocalDate invoiceStartDate;

        @Before
        public void setUp() throws Exception {
            applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancyForGb.PATH);
            seller = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
            buyer = partyRepository.findPartyByReference(OrganisationForPoisonGb.REF);
            lease = leaseRepository.findLeaseByReference(LeaseForOxfPoison003Gb.REF);
            invoiceStartDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);
        }

        @Test
        public void whenDoesNotExist() {
            // given
            Assert.assertThat(invoiceRepository.allInvoices().isEmpty(), is(true));
            // when
            InvoiceForLease invoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, invoiceStartDate, null);
            // then
            Assert.assertNotNull(invoice);
            Assert.assertThat(invoiceRepository.allInvoices().isEmpty(), is(false));
        }

        @Test
        public void whenExist() {
            // given
            InvoiceForLease invoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, invoiceStartDate, null);
            // when
            InvoiceForLease invoice2 = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, invoiceStartDate, null);
            // then
            Assert.assertThat(invoice2, is(sameInstance(invoice)));
        }

    }

    public static class InvoiceForLeaseImportTest extends InvoiceRepository_IntegTest {

        @Before
        public void setup() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                }
            });

        }

        String leaseReference;
        LocalDate dueDate;
        String paymentMethodStr;
        String itemDescription;
        String itemChargeReference;
        BigDecimal netAmount;
        LocalDate itemStartDate;
        LocalDate itemEndDate;

        @Test
        public void importInvoiceLine() throws Exception {

            // given
            leaseReference = LeaseForOxfPoison003Gb.REF;
            dueDate = new LocalDate(2016, 07, 01);
            itemStartDate = new LocalDate(2015, 01, 01);
            itemEndDate = new LocalDate(2015, 12, 31);
            paymentMethodStr = "DIRECT_DEBIT";
            itemChargeReference = ChargeRefData.IT_SERVICE_CHARGE;
            itemDescription = "Some description";
            netAmount = new BigDecimal("100.23");

            InvoiceImportLine invoiceImportLine = new InvoiceImportLine(
                    leaseReference,
                    dueDate,
                    paymentMethodStr,
                    itemChargeReference,
                    itemDescription,
                    netAmount,
                    itemStartDate,
                    itemEndDate,
                    null);

            InvoiceImportLine invoiceImportLine2 = new InvoiceImportLine(
                    leaseReference,
                    dueDate,
                    paymentMethodStr,
                    itemChargeReference,
                    null,
                    netAmount,
                    itemStartDate,
                    itemEndDate,
                    null);

            // when
            wrap(invoiceImportLine).importData();
            wrap(invoiceImportLine2).importData();

            // then

            // two import lines create to invoices
            List<Invoice> newInvoices = invoiceRepository.findByStatus(InvoiceStatus.NEW);
            assertThat(newInvoices.size()).isEqualTo(2);

            // for first invoice
            Invoice<?> invoice = newInvoices.get(0);
            assertThat(invoice.getDueDate()).isEqualTo(dueDate);
            assertThat(invoice.getTotalNetAmount()).isEqualTo(netAmount);
            assertThat(invoice.getCurrency().getReference()).isEqualTo(CurrenciesRefData.EUR);

            InvoiceItem item = invoice.getItems().first();
            assertThat(item.getDescription()).isEqualTo(itemDescription);
            assertThat(item.getStartDate()).isEqualTo(itemStartDate);
            assertThat(item.getEndDate()).isEqualTo(itemEndDate);
            assertThat(item.getCharge().getReference()).isEqualTo(itemChargeReference);
            assertThat(item.getQuantity()).isEqualTo(BigDecimal.ONE);

            // second invoice item defaults to charge description
            Invoice<?> invoice2 = newInvoices.get(1);
            InvoiceItem item2 = invoice2.getItems().first();
            assertThat(item2.getDescription()).isEqualTo(item2.getCharge().getDescription());

        }

    }

}