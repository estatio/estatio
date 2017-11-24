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
package org.estatio.module.lease.dom.invoicing;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.base.platform.docfragment.FragmentRenderService;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.numerator.dom.Numerator;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceForLease_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    InvoiceForLease invoice;

    Numerator numerator;

    @Mock
    InvoiceRepository mockInvoiceRepository;

    @Mock
    NumeratorForCollectionRepository mockNumeratorRepository;

    @Mock
    ClockService mockClockService;

    @Mock
    TitleService mockTitleService;

    @Mock
    MessageService mockMessageService;

    @Mock
    FragmentRenderService mockFragmentRenderService;

    @Mock
    Lease lease;

    @Mock
    Property invoiceProperty;

    @Mock
    @Ignoring
    DomainObjectContainer mockContainer;

    ApplicationTenancy applicationTenancy;

    @Before
    public void setUp() throws Exception {
        numerator = new Numerator();
        numerator.setFormat("XXX-%05d");
        numerator.setLastIncrement(BigInteger.TEN);
        applicationTenancy = new ApplicationTenancy();
        applicationTenancy.setPath("/");

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(LocalDate.now()));
            }
        });

    }

    void allowingMockInvoicesToReturnNumerator(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockNumeratorRepository).findInvoiceNumberNumerator(with(any(Property.class)), with(any(ApplicationTenancy.class)));
                will(returnValue(numerator));
            }
        });
    }

    void allowingMockInvoicesToReturnCollectionNumerator(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockNumeratorRepository).findCollectionNumberNumerator();
                will(returnValue(numerator));
            }
        });
    }

    void allowingMockInvoicesToReturnInvoice(final String invoiceNumber, final LocalDate invoiceDate) {
        context.checking(new Expectations() {
            {
                allowing(mockInvoiceRepository).findMatchingInvoiceNumber(with(any(String.class)));
                will(returnValue(Arrays.asList(new InvoiceForLease() {
                    @Override
                    public String getInvoiceNumber() {
                        return invoiceNumber;
                    }

                    ;

                    @Override
                    public LocalDate getInvoiceDate() {
                        return invoiceDate;
                    }

                    ;
                })));
            }
        });
    }

    InvoiceForLease createInvoice(final FixedAsset fixedAsset, final InvoiceStatus invoiceStatus) {
        final InvoiceForLease invoice = new InvoiceForLease() {
            @Override
            public FixedAsset getFixedAsset() {
                return fixedAsset;
            }

            @Override public ApplicationTenancy getApplicationTenancy() {
                return applicationTenancy;
            }
        };
        invoice.setStatus(invoiceStatus);
        invoice.setContainer(mockContainer);
        invoice.numeratorRepository = mockNumeratorRepository;
        invoice.clockService = mockClockService;
        return invoice;
    }

    public static class AssignInvoiceNumber_Test extends InvoiceForLease_Test {


        @Test
        public void happyCase_whenNoInvoiceNumberPreviouslyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-00010", new LocalDate(2012, 1, 1));
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            // when
            final InvoiceForLease._invoice invoice_invoice = new InvoiceForLease._invoice(invoice);
            invoice_invoice.numeratorRepository = mockNumeratorRepository;
            invoice_invoice.titleService = mockTitleService;
            invoice_invoice.messageService = mockMessageService;

            // expect
            context.checking(new Expectations() {{
                allowing(mockTitleService).titleOf(invoice);
                will(returnValue("Invoice #001"));

                oneOf(mockMessageService).informUser("Assigned XXX-00011 to invoice Invoice #001");

            }});

            assertThat(invoice_invoice.disable$$()).isNull();
            invoice_invoice.$$(mockClockService.now());

            assertThat(this.invoice.getInvoiceNumber()).isEqualTo("XXX-00011");
            assertThat(this.invoice.getStatus()).isEqualTo(InvoiceStatus.INVOICED);
        }

        @Test
        public void whenInvoiceNumberAlreadyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);
            invoice.setInvoiceNumber("SOME-INVOICE-NUMBER");

            // when
            final InvoiceForLease._invoice invoice_invoice = new InvoiceForLease._invoice(this.invoice);
            assertThat(invoice_invoice.disable$$()).isEqualTo("Invoice number already assigned");
            invoice_invoice.$$(mockClockService.now());

            assertThat(invoice.getInvoiceNumber()).isEqualTo("SOME-INVOICE-NUMBER");
        }

        @Test
        public void whenNoProperty() {

            allowingMockInvoicesToReturnNumerator(null);
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            // when
            final InvoiceForLease._invoice invoice_invoice = new InvoiceForLease._invoice(this.invoice);
            invoice_invoice.numeratorRepository = mockNumeratorRepository;

            assertThat(invoice_invoice.disable$$()).isEqualTo("No 'invoice number' numerator found for invoice's property");

            invoice_invoice.$$(mockClockService.now());
            assertThat(invoice.getInvoiceNumber()).isNull();
        }

        @Test
        public void whenNotInCollectedState() {

            allowingMockInvoicesToReturnNumerator(null);
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            final InvoiceForLease._invoice invoice_invoice = new InvoiceForLease._invoice(this.invoice);
            invoice_invoice.numeratorRepository = mockNumeratorRepository;

            // when
            assertThat(invoice_invoice.disable$$()).isEqualTo("No 'invoice number' numerator found for invoice's property");

            invoice_invoice.$$(mockClockService.now());
            assertThat(invoice.getInvoiceNumber()).isNull();
        }

    }

    public static class Collect_Test extends InvoiceForLease_Test {

        private InvoiceForLease createInvoice(final Property property, final PaymentMethod paymentMethod, final InvoiceStatus status) {
            final InvoiceForLease invoice = new InvoiceForLease() {

                @Override
                public PaymentMethod getPaymentMethod() {
                    return paymentMethod;
                }

                @Override
                public InvoiceStatus getStatus() {
                    return status;
                }

                @Override public ApplicationTenancy getApplicationTenancy() {
                    return applicationTenancy;
                }
            };
            invoice.setContainer(mockContainer);
            invoice.numeratorRepository = mockNumeratorRepository;
            return invoice;
        }

        @Test
        public void happyCase_directDebit_and_collected_andWhenNoInvoiceNumberPreviouslyAssigned() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);
            context.checking(new Expectations() {
                {
                    allowing(lease).getPaidBy();
                    will(returnValue(new BankMandate() {
                        public FinancialAccount getBankAccount() {
                            return new BankAccount() {
                                public boolean isValidIban() {
                                    return true;
                                }

                                ;
                            };
                        }

                        ;
                    }));
                }
            });

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
            invoice.setLease(lease);

            final InvoiceForLease._collect invoice_collect = new InvoiceForLease._collect(invoice);
            invoice_collect.numeratorRepository = mockNumeratorRepository;


            assertThat(invoice_collect.hide$$()).isFalse();
            assertThat(invoice_collect.disable$$()).isNull();
            invoice_collect.doCollect();

            assertThat(invoice.getCollectionNumber()).isEqualTo("XXX-00011");
        }

        @Test
        public void whenNoMandateAssigned() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
            invoice.setLease(new Lease());

            final InvoiceForLease._collect invoice_collect = new InvoiceForLease._collect(invoice);
            invoice_collect.numeratorRepository = mockNumeratorRepository;

            assertThat(invoice_collect.hide$$()).isFalse();
            assertThat(invoice_collect.disable$$()).isEqualTo("No mandate assigned to invoice's lease");
            invoice_collect.doCollect();

            assertThat(invoice.getCollectionNumber()).isNull();
        }

        @Test
        public void whenInvoiceNumberAlreadyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

            invoice.setCollectionNumber("SOME-COLLECTION-NUMBER");

            final InvoiceForLease._collect invoice_collect = new InvoiceForLease._collect(invoice);
            assertThat(invoice_collect.hide$$()).isFalse();
            assertThat(invoice_collect.disable$$()).isEqualTo("Collection number already assigned");
            invoice_collect.doCollect();

            assertThat(invoice.getCollectionNumber()).isEqualTo("SOME-COLLECTION-NUMBER");
        }

        @Test
        public void whenNoProperty() {
            allowingMockInvoicesToReturnCollectionNumerator(null);

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

            final InvoiceForLease._collect invoice_collect = new InvoiceForLease._collect(invoice);
            invoice_collect.numeratorRepository = mockNumeratorRepository;

            assertThat(invoice_collect.hide$$()).isFalse();
            assertThat(invoice_collect.disable$$()).isEqualTo("No 'collection number' numerator found for invoice's property");

            invoice_collect.doCollect();
            assertThat(invoice.getCollectionNumber()).isNull();
        }

        @Test
        public void whenNotDirectDebit() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            invoice = createInvoice(invoiceProperty, PaymentMethod.BANK_TRANSFER, InvoiceStatus.APPROVED);
            invoice.setLease(new Lease());

            final InvoiceForLease._collect invoice_collect = new InvoiceForLease._collect(invoice);
            invoice_collect.numeratorRepository = mockNumeratorRepository;

            assertThat(invoice_collect.hide$$()).isTrue();
            assertThat(invoice_collect.disable$$()).isEqualTo("No mandate assigned to invoice's lease");

            invoice_collect.doCollect();

            assertThat(invoice.getCollectionNumber()).isNull();
        }

        @Test
        public void whenNotCollected() {
            // given
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            // when
            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.NEW);

            // then
            final InvoiceForLease._collect invoice_collect = new InvoiceForLease._collect(invoice);
            invoice_collect.numeratorRepository = mockNumeratorRepository;

            assertThat(invoice_collect.hide$$()).isFalse();
            assertThat(invoice_collect.disable$$()).isEqualTo("Must be in status of 'approved'");

            // and
            invoice_collect.doCollect();

            // then
            assertThat(invoice.getCollectionNumber()).isNull();
        }

    }

    public static class CompareTo_Test extends ComparableContractTest_compareTo<InvoiceForLease> {

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<InvoiceForLease>> orderedTuples() {
            return listOf(listOf(
                    newInvoice(null),
                    newInvoice("0000123"),
                    newInvoice("0000123"),
                    newInvoice("0000124")));
        }

        private InvoiceForLease newInvoice(String number) {
            final InvoiceForLease inv = new InvoiceForLease();
            inv.setInvoiceNumber(number);
            return inv;
        }
    }

    public static class ValidInvoiceDate_Test extends InvoiceForLease_Test {

        InvoiceForLease._invoice invoice_invoice;

        @Before
        public void setUp() throws Exception {
            invoice = new InvoiceForLease() {
                @Override public ApplicationTenancy getApplicationTenancy() {
                    return new ApplicationTenancy();
                }

                ;
            };
            invoice.setDueDate(new LocalDate(2012, 2, 2));
            invoice.numeratorRepository = mockNumeratorRepository;
            invoice.setFixedAsset(invoiceProperty);

            numerator = new Numerator();
            numerator.setLastIncrement(BigInteger.TEN);
            numerator.setFormat("XXX-%05d");
            applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath("/");

            invoice_invoice = new InvoiceForLease._invoice(this.invoice);
            invoice_invoice.numeratorRepository = mockNumeratorRepository;
            invoice_invoice.invoiceRepository = mockInvoiceRepository;
        }

        @Test
        public void invoiceDateIsAfterDueDate() {
            assertThat(invoice_invoice.validInvoiceDate(new LocalDate(2012, 2, 3))).isNotNull();
        }

        @Test
        public void invoiceDateIsBeforeDueDate() {
            // given
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-0010", new LocalDate(2012, 1, 1));
            assertThat(invoice.getApplicationTenancy()).isNotNull();

            // when,then
            assertThat(invoice_invoice.validInvoiceDate(new LocalDate(2012, 2, 1))).isNull();
        }

        @Test
        public void invoice_date_cannot_be_after_last_invoice_date() throws Exception {
            // given
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-0010", new LocalDate(2012, 1, 2));
            assertThat(invoice.getApplicationTenancy()).isNotNull();

            // when,then
            assertThat(invoice_invoice.validInvoiceDate(new LocalDate(2012, 1, 1))).isEqualTo("Invoice number XXX-0010 has an invoice date 2012-01-02 which is after 2012-01-01");
        }
    }

    public static class NewItem_Test extends InvoiceForLease_Test {

        @Test
        public void disabled_when_immutable() throws Exception {
            //Given
            InvoiceForLease invoice = new InvoiceForLease();
            invoice.setStatus(InvoiceStatus.INVOICED);
            // When, Then
            final InvoiceForLease._newItem invoice_newItem = new InvoiceForLease._newItem(invoice);
            assertThat(invoice_newItem.disable$$()).isNotNull();
        }
    }

    public static class ValidateNewItemWithEmptyDates_Test extends InvoiceForLease_Test {

        @Test
        public void giveWarningForEmptyDates() throws Exception {

            //Given
            InvoiceForLease invoice = new InvoiceForLease();

            final InvoiceForLease._newItem invoice_newItem = new InvoiceForLease._newItem(invoice);
            invoice_newItem.messageService = mockMessageService;

            // expect
            context.checking(new Expectations() {{
                oneOf(mockMessageService).warnUser("Both start date and end date are empty. Is this done intentionally?");
            }});

            // when
            invoice_newItem.validate$$(null, null, null, null, null);

        }
    }

    public static class ValidateNewItemWithEmptyStartDate_Test extends InvoiceForLease_Test {


        @Test
        public void giveWarningForEmptyStartDate() throws Exception {

            //Given
            InvoiceForLease invoice = new InvoiceForLease();

            final InvoiceForLease._newItem invoice_newItem = new InvoiceForLease._newItem(invoice);
            invoice_newItem.messageService = mockMessageService;

            // expect
            context.checking(new Expectations() {{
                oneOf(mockMessageService).warnUser("Start date is empty. Is this done intentionally?");
            }});

            invoice_newItem.validate$$(null, null, null, null, new LocalDate(2000, 01, 01));
        }
    }



}