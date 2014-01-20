/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.asset.Property;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.numerator.Numerator;

public class InvoiceTest_collect {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    Invoices mockInvoices;

    @Ignoring
    @Mock
    DomainObjectContainer mockContainer;

    @Ignoring
    @Mock
    Property invoiceProperty;

    @Mock
    Lease lease;

    private Invoice invoice;
    
    private Numerator numerator;

    @Before
    public void setUp() throws Exception {

        numerator = new Numerator();
        numerator.setFormat("XXX-%05d");
        numerator.setLastIncrement(BigInteger.TEN);

    }

    private Invoice createInvoice(final Property property, final PaymentMethod paymentMethod, final InvoiceStatus status) {
        final Invoice invoice = new Invoice() {
            public Property getProperty() {
                return property;
            }

            @Override
            public PaymentMethod getPaymentMethod() {
                return paymentMethod;
            }

            @Override
            public InvoiceStatus getStatus() {
                return status;
            }
        };
        invoice.setContainer(mockContainer);
        invoice.injectInvoices(mockInvoices);
        return invoice;
    }

    @Test
    public void happyCase_directDebit_and_collected_andWhenNoInvoiceNumberPreviouslyAssigned() {
        allowingMockInvoicesRepoToReturn(numerator);
        context.checking(new Expectations() {
            {
                allowing(lease).getPaidBy();
                will(returnValue(new BankMandate()));
            }
        });
        
        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
        invoice.setLease(lease);
        
        assertThat(invoice.hideCollect(), is(false));
        assertNull(invoice.disableCollect());
        invoice.collect();

        assertThat(invoice.getCollectionNumber(), is("XXX-00011"));
    }

    @Test
    public void whenNoMandateAssigned() {
        allowingMockInvoicesRepoToReturn(numerator);
        
        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
        invoice.setLease(new Lease());
        
        assertThat(invoice.hideCollect(), is(false));
        assertThat(invoice.disableCollect(), is("No mandate assigned to invoice's lease"));
        invoice.collect();
        assertNull(invoice.getCollectionNumber());
    }
    
    @Test
    public void whenInvoiceNumberAlreadyAssigned() {
        allowingMockInvoicesRepoToReturn(numerator);

        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

        invoice.setCollectionNumber("SOME-COLLECTION-NUMBER");

        assertThat(invoice.hideCollect(), is(false));
        assertThat(invoice.disableCollect(), is("Collection number already assigned"));
        invoice.collect();

        assertThat(invoice.getCollectionNumber(), is("SOME-COLLECTION-NUMBER"));
    }

    @Test
    public void whenNoProperty() {

        allowingMockInvoicesRepoToReturn(null);

        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

        assertThat(invoice.hideCollect(), is(false));
        assertThat(invoice.disableCollect(), is("No 'collection number' numerator found for invoice's property"));

        invoice.collect();
        assertThat(invoice.getCollectionNumber(), is(nullValue()));
    }

    @Test
    public void whenNotDirectDebit() {
        allowingMockInvoicesRepoToReturn(numerator);

        invoice = createInvoice(invoiceProperty, PaymentMethod.BANK_TRANSFER, InvoiceStatus.APPROVED);
        invoice.setLease(new Lease());

        assertThat(invoice.hideCollect(), is(true));
        assertThat(invoice.disableCollect(), is("No mandate assigned to invoice's lease"));

        invoice.collect();

        assertThat(invoice.getCollectionNumber(), is(nullValue()));
    }

    @Test
    public void whenNotCollected() {
        allowingMockInvoicesRepoToReturn(numerator);

        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.NEW);

        assertThat(invoice.hideCollect(), is(false));
        assertThat(invoice.disableCollect(), is("Must be in status of 'approved'"));

        invoice.collect();

        assertThat(invoice.getCollectionNumber(), is(nullValue()));
    }

    private void allowingMockInvoicesRepoToReturn(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockInvoices).findCollectionNumberNumerator();
                will(returnValue(numerator));
            }
        });
    }
}
