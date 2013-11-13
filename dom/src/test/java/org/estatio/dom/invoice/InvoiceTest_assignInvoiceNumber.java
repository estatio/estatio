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
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.asset.Property;
import org.estatio.dom.numerator.Numerator;
import org.estatio.services.clock.ClockService;

public class InvoiceTest_assignInvoiceNumber {

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

    private Invoice invoice;

    private Numerator numerator;

    @Mock
    private ClockService mockClockService;

    @Before
    public void setUp() throws Exception {
        numerator = new Numerator();
        numerator.setFormat("XXX-%05d");
        numerator.setLastIncrement(BigInteger.TEN);

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(LocalDate.now()));
            }
        });

    }

    private Invoice createInvoice(final Property property, final InvoiceStatus invoiceStatus) {
        final Invoice invoice = new Invoice() {
            @Override
            public Property getProperty() {
                return property;
            }
        };
        invoice.setStatus(invoiceStatus);
        invoice.setContainer(mockContainer);
        invoice.injectInvoices(mockInvoices);
        invoice.injectClockService(mockClockService);
        return invoice;
    }

    @Test
    public void happyCase_whenNoInvoiceNumberPreviouslyAssigned() {
        allowingMockInvoicesRepoToReturn(numerator);
        invoice = createInvoice(invoiceProperty, InvoiceStatus.COLLECTED);

        assertThat(invoice.hideInvoiceNow(), is(false));
        assertThat(invoice.disableInvoiceNow(), is(nullValue()));
        invoice.invoiceNow();

        assertThat(invoice.getInvoiceNumber(), is("XXX-00011"));
        assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
    }

    @Test
    public void whenInvoiceNumberAlreadyAssigned() {
        allowingMockInvoicesRepoToReturn(numerator);
        invoice = createInvoice(invoiceProperty, InvoiceStatus.COLLECTED);
        invoice.setInvoiceNumber("SOME-INVOICE-NUMBER");

        assertThat(invoice.hideInvoiceNow(), is(false));
        assertThat(invoice.disableInvoiceNow(), is("Invoice number already assigned"));
        invoice.invoiceNow();

        assertThat(invoice.getInvoiceNumber(), is("SOME-INVOICE-NUMBER"));
    }

    @Test
    public void whenNoProperty() {

        allowingMockInvoicesRepoToReturn(null);
        invoice = createInvoice(invoiceProperty, InvoiceStatus.COLLECTED);

        assertThat(invoice.hideInvoiceNow(), is(false));
        assertThat(invoice.disableInvoiceNow(), is("No 'invoice number' numerator found for invoice's property"));

        invoice.invoiceNow();
        assertThat(invoice.getInvoiceNumber(), is(nullValue()));
    }

    @Test
    public void whenNotInCollectedState() {

        allowingMockInvoicesRepoToReturn(null);
        invoice = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

        assertThat(invoice.hideInvoiceNow(), is(false));
        assertThat(invoice.disableInvoiceNow(), is("No 'invoice number' numerator found for invoice's property"));

        invoice.invoiceNow();
        assertThat(invoice.getInvoiceNumber(), is(nullValue()));
    }

    private void allowingMockInvoicesRepoToReturn(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockInvoices).findInvoiceNumberNumerator(with(any(Property.class)));
                will(returnValue(numerator));
            }
        });
    }

}
