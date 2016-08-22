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
package org.estatio.dom.invoice;

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
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.numerator.Numerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class InvoiceTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Invoice invoice;

    Numerator numerator;

    @Mock
    InvoiceRepository mockInvoiceRepository;

    @Mock
    EstatioNumeratorRepository mockEstatioNumeratorRepository;

    @Mock
    ClockService mockClockService;

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
                allowing(mockEstatioNumeratorRepository).findInvoiceNumberNumerator(with(any(Property.class)), with(any(ApplicationTenancy.class)));
                will(returnValue(numerator));
            }
        });
    }

    void allowingMockInvoicesToReturnCollectionNumerator(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockEstatioNumeratorRepository).findCollectionNumberNumerator();
                will(returnValue(numerator));
            }
        });
    }

    void allowingMockInvoicesToReturnInvoice(final String invoiceNumber, final LocalDate invoiceDate) {
        context.checking(new Expectations() {
            {
                allowing(mockInvoiceRepository).findByInvoiceNumber(with(any(String.class)));
                will(returnValue(Arrays.asList(new Invoice() {
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

    Invoice createInvoice(final FixedAsset fixedAsset, final InvoiceStatus invoiceStatus) {
        final Invoice invoice = new Invoice() {
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
        invoice.invoiceRepository = mockInvoiceRepository;
        invoice.estatioNumeratorRepository = mockEstatioNumeratorRepository;
        invoice.clockService = mockClockService;
        return invoice;
    }

    public static class AssignInvoiceNumber extends InvoiceTest {

        @Test
        public void happyCase_whenNoInvoiceNumberPreviouslyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-00010", new LocalDate(2012, 1, 1));
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            assertThat(invoice.disableInvoice(null), is(nullValue()));
            invoice.invoice(mockClockService.now());

            assertThat(invoice.getInvoiceNumber(), is("XXX-00011"));
            assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
        }

        @Test
        public void whenInvoiceNumberAlreadyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);
            invoice.setInvoiceNumber("SOME-INVOICE-NUMBER");

            assertThat(invoice.disableInvoice(null), is("Invoice number already assigned"));
            invoice.invoice(mockClockService.now());

            assertThat(invoice.getInvoiceNumber(), is("SOME-INVOICE-NUMBER"));
        }

        @Test
        public void whenNoProperty() {

            allowingMockInvoicesToReturnNumerator(null);
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            assertThat(invoice.disableInvoice(null), is("No 'invoice number' numerator found for invoice's property"));

            invoice.invoice(mockClockService.now());
            assertThat(invoice.getInvoiceNumber(), is(nullValue()));
        }

        @Test
        public void whenNotInCollectedState() {

            allowingMockInvoicesToReturnNumerator(null);
            invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            assertThat(invoice.disableInvoice(null), is("No 'invoice number' numerator found for invoice's property"));

            invoice.invoice(mockClockService.now());
            assertThat(invoice.getInvoiceNumber(), is(nullValue()));
        }

    }

    public static class Collect extends InvoiceTest {

        private Invoice createInvoice(final Property property, final PaymentMethod paymentMethod, final InvoiceStatus status) {
            final Invoice invoice = new Invoice() {

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
            invoice.invoiceRepository = mockInvoiceRepository;
            invoice.estatioNumeratorRepository = mockEstatioNumeratorRepository;
            return invoice;
        }

        @Test
        public void happyCase_directDebit_and_collected_andWhenNoInvoiceNumberPreviouslyAssigned() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);
            context.checking(new Expectations() {
                {
                    allowing(lease).getPaidBy();
                    will(returnValue(new BankMandate() {
                        public org.estatio.dom.financial.FinancialAccount getBankAccount() {
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

            assertThat(invoice.hideCollect(), is(false));
            assertNull(invoice.disableCollect());
            invoice.doCollect();

            assertThat(invoice.getCollectionNumber(), is("XXX-00011"));
        }

        @Test
        public void whenNoMandateAssigned() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
            invoice.setLease(new Lease());

            assertThat(invoice.hideCollect(), is(false));
            assertThat(invoice.disableCollect(), is("No mandate assigned to invoice's lease"));
            invoice.doCollect();
            assertNull(invoice.getCollectionNumber());
        }

        @Test
        public void whenInvoiceNumberAlreadyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

            invoice.setCollectionNumber("SOME-COLLECTION-NUMBER");

            assertThat(invoice.hideCollect(), is(false));
            assertThat(invoice.disableCollect(), is("Collection number already assigned"));
            invoice.doCollect();

            assertThat(invoice.getCollectionNumber(), is("SOME-COLLECTION-NUMBER"));
        }

        @Test
        public void whenNoProperty() {
            allowingMockInvoicesToReturnCollectionNumerator(null);

            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

            assertThat(invoice.hideCollect(), is(false));
            assertThat(invoice.disableCollect(), is("No 'collection number' numerator found for invoice's property"));

            invoice.doCollect();
            assertThat(invoice.getCollectionNumber(), is(nullValue()));
        }

        @Test
        public void whenNotDirectDebit() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            invoice = createInvoice(invoiceProperty, PaymentMethod.BANK_TRANSFER, InvoiceStatus.APPROVED);
            invoice.setLease(new Lease());

            assertThat(invoice.hideCollect(), is(true));
            assertThat(invoice.disableCollect(), is("No mandate assigned to invoice's lease"));

            invoice.doCollect();

            assertThat(invoice.getCollectionNumber(), is(nullValue()));
        }

        @Test
        public void whenNotCollected() {
            // given
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            // when
            invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.NEW);

            // then
            assertThat(invoice.hideCollect(), is(false));
            assertThat(invoice.disableCollect(), is("Must be in status of 'approved'"));

            // and
            invoice.doCollect();

            // then
            assertThat(invoice.getCollectionNumber(), is(nullValue()));
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<Invoice> {

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<Invoice>> orderedTuples() {
            return listOf(listOf(
                    newInvoice(null),
                    newInvoice("0000123"),
                    newInvoice("0000123"),
                    newInvoice("0000124")));
        }

        private Invoice newInvoice(String number) {
            final Invoice inv = new Invoice();
            inv.setInvoiceNumber(number);
            return inv;
        }

    }

    public static class ValidInvoiceDate extends InvoiceTest {

        @Before
        public void setUp() throws Exception {
            invoice = new Invoice() {
                @Override public ApplicationTenancy getApplicationTenancy() {
                    return new ApplicationTenancy();
                }

                ;
            };
            invoice.setDueDate(new LocalDate(2012, 2, 2));
            invoice.invoiceRepository = mockInvoiceRepository;
            invoice.estatioNumeratorRepository = mockEstatioNumeratorRepository;
            invoice.setFixedAsset(invoiceProperty);
        }

        @Test
        public void invoiceDateIsAfterDueDate() {
            assertFalse(invoice.validInvoiceDate(new LocalDate(2012, 2, 3)));
        }

        @Test
        public void invoiceDateIsBeforeDueDate() {
            // given
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-0010", new LocalDate(2012, 1, 1));
            assertNotNull("App tenancy can't be null", invoice.getApplicationTenancy());

            // when,then
            assertTrue(invoice.validInvoiceDate(new LocalDate(2012, 2, 1)));
        }

    }

    public static class NewItem extends InvoiceTest {

        @Test
        public void disabled_when_immutable() throws Exception {
            //Given
            Invoice invoice = new Invoice();
            invoice.setStatus(InvoiceStatus.INVOICED);
            // When, Then
            assertThat(invoice.disableNewItem(null,null,null,null,null), notNullValue());

        }


    }

    public static class ValidateNewItemWithEmptyDates extends InvoiceTest {

        @Mock
        DomainObjectContainer mockContainer2;

        String s = "Both start date and end date are empty. Is this done intentionally?";

        @Before
        public void setup() {
            context.checking(new Expectations() {
                {
                    atLeast(1).of(mockContainer2).warnUser(s);
                }
            });
        }

        @Test
        public void giveWarningForEmptyDates() throws Exception {
            //Given
            Invoice invoice = new Invoice();
            invoice.setContainer(mockContainer2);

            //When
            //Then
            invoice.validateNewItem(null, null, null, null, null);

        }

    }

    public static class ValidateNewItemWithEmptyStartDate extends InvoiceTest {

        @Mock
        DomainObjectContainer mockContainer2;

        String s = "Start date is empty. Is this done intentionally?";

        @Before
        public void setup() {
            context.checking(new Expectations() {
                {
                    atLeast(1).of(mockContainer2).warnUser(s);
                }
            });
        }

        @Test
        public void giveWarningForEmptyStartDate() throws Exception {
            //Given
            Invoice invoice = new Invoice();
            invoice.setContainer(mockContainer2);

            //When
            //Then
            invoice.validateNewItem(null, null, null, null, new LocalDate(2000,01,01));

        }

    }

}