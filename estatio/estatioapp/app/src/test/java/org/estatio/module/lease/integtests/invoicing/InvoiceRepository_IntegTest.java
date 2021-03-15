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
package org.estatio.module.lease.integtests.invoicing;

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

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.base.integtests.VT;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.app.NumeratorForOutgoingInvoicesMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.imports.InvoiceImportLine;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class InvoiceRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    NumeratorForOutgoingInvoicesMenu numeratorForOutgoingInvoicesMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    BookmarkService bookmarkService;

    public static class CreateInvoiceNumberNumerator extends InvoiceRepository_IntegTest {


        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.KalNl.builder());
                }
            });
        }

        private Property propertyOxf;
        private Organisation seller;
        private Property propertyKal;

        private Bookmark propertyOxfBookmark;

        @Before
        public void setUp() {
            propertyOxf = Property_enum.OxfGb.findUsing(serviceRegistry);
            propertyKal = Property_enum.KalNl.findUsing(serviceRegistry);
            seller = PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.getOwner_d().findUsing(serviceRegistry);

            propertyOxfBookmark = bookmarkService.bookmarkFor(propertyOxf);
        }

        @Test
        public void whenNoneForProperty() {

            // given
            Numerator numerator = numeratorForOutgoingInvoicesMenu.findInvoiceNumberNumerator(propertyOxf, seller
            );
            Assert.assertNull(numerator);

            // when
            numerator = numeratorForOutgoingInvoicesMenu
                    .createInvoiceNumberNumerator(propertyOxf, seller, "OXF-%05d", BigInteger.TEN);

            // then
            Assert.assertNotNull(numerator);
            assertThat(numerator.getName(), is(NumeratorForOutgoingInvoicesRepository.INVOICE_NUMBER));
            assertThat(numerator.getObjectType(), is(propertyOxfBookmark.getObjectType()));
            assertThat(numerator.getObjectIdentifier(), is(propertyOxfBookmark.getIdentifier()));
            assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
        }

        @Test
        public void canCreateOnePerProperty() {

            // given
            Numerator numerator1 = numeratorForOutgoingInvoicesMenu.createInvoiceNumberNumerator(propertyOxf, seller,
                    "OXF-%05d", BigInteger.TEN);
            Assert.assertNotNull(numerator1);

            // when
            Numerator numerator2 = numeratorForOutgoingInvoicesMenu.createInvoiceNumberNumerator(propertyKal, seller,
                    "KAL-%05d", BigInteger.ZERO);

            // then
            Assert.assertNotNull(numerator2);
            assertThat(numerator1, is(not(numerator2)));

            assertThat(numerator1.nextIncrementStr(), is("OXF-00011"));
            assertThat(numerator2.nextIncrementStr(), is("KAL-00001"));
            assertThat(numerator2.nextIncrementStr(), is("KAL-00002"));
            assertThat(numerator1.nextIncrementStr(), is("OXF-00012"));
        }

        @Test
        public void canOnlyCreateOnePerProperty_andCannotReset() {

            // given
            Numerator numerator1 = numeratorForOutgoingInvoicesMenu.createInvoiceNumberNumerator(propertyOxf, seller,
                    "OXF-%05d", BigInteger.TEN);
            Assert.assertNotNull(numerator1);

            assertThat(numerator1.nextIncrementStr(), is("OXF-00011"));

            // when
            Numerator numerator2 = numeratorForOutgoingInvoicesMenu.createInvoiceNumberNumerator(propertyOxf, seller,
                    "KAL-%05d", BigInteger.ZERO);

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
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                }
            });
        }

        private Property propertyOxf;
        private Organisation seller;

        @Before
        public void setUp() {
            propertyOxf = Property_enum.OxfGb.findUsing(serviceRegistry);
            seller = PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.getOwner_d().findUsing(serviceRegistry);
        }

        @Test
        public void whenNone() {
            // when
            Numerator numerator = numeratorForOutgoingInvoicesMenu.findInvoiceNumberNumerator(propertyOxf, seller
            );
            // then
            Assert.assertNull(numerator);
        }

    }


    public static class FindInvoiceRepositoryTest extends InvoiceRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, InvoiceForLease_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, InvoiceForLease_enum.KalPoison001Nl.builder());
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
        public void setUp() {
            applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancy_enum.Nl.getPath());
            seller = InvoiceForLease_enum.KalPoison001Nl.getSeller_d().findUsing(serviceRegistry);
            buyer = InvoiceForLease_enum.KalPoison001Nl.getBuyer_d().findUsing(serviceRegistry);
            lease = InvoiceForLease_enum.KalPoison001Nl.getLease_d().findUsing(serviceRegistry);

            propertyKal = Property_enum.KalNl.findUsing(serviceRegistry);

            InvoiceForLease invoiceForLease = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller,
                    buyer,
                    PaymentMethod.DIRECT_DEBIT,
                    lease,
                    InvoiceStatus.NEW,
                    InvoiceForLease_enum.KalPoison001Nl.getDueDate(),
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
                InvoiceForLease invoice = invoiceForLeaseRepository.findMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                        InvoiceForLease_enum.KalPoison001Nl.getDueDate());
                assertNotNull(invoice);
            }
        }

        public static class BySendToTest extends FindInvoiceRepositoryTest {

            @Inject
            RepositoryService repositoryService;
            CommunicationChannel communicationChannel0;
            CommunicationChannel communicationChannel1;

            @Before
            public void lookupCommChannel() {
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
                    executionContext.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb,
                            InvoiceForLease_enum.KalPoison001Nl);
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        @Before
        public void setUp() {
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(
                    ApplicationTenancy_enum.Gb.getPath());
            final Party seller = InvoiceForLease_enum.OxfPoison003Gb.getSeller_d().findUsing(serviceRegistry);
            final Party buyer = InvoiceForLease_enum.OxfPoison003Gb.getBuyer_d().findUsing(serviceRegistry);
            final Lease lease = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().findUsing(serviceRegistry);
            final LocalDate startDate = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().getStartDate().plusYears(1);

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
                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());
                }
            });
        }

        private ApplicationTenancy applicationTenancy;
        private Party seller;
        private Party buyer;
        private Lease lease;
        private LocalDate invoiceStartDate;

        @Before
        public void setUp() {
            applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancy_enum.Gb.getPath());
            seller = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
            buyer = OrganisationAndComms_enum.PoisonGb.findUsing(serviceRegistry);
            lease = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);
            invoiceStartDate = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().getStartDate().plusYears(1);
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
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());

                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());
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
        public void importInvoiceLine() {

            // given
            leaseReference = Lease_enum.OxfPoison003Gb.getRef();
            dueDate = VT.ld(2016, 7, 1);
            itemStartDate = VT.ld(2015, 1, 1);
            itemEndDate = VT.ld(2015, 12, 31);
            paymentMethodStr = "DIRECT_DEBIT";
            itemChargeReference = Charge_enum.ItServiceCharge.getRef();
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
                    null,
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
                    null,
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
            assertThat(invoice.getCurrency().getReference()).isEqualTo(Currency_enum.EUR.getReference());

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