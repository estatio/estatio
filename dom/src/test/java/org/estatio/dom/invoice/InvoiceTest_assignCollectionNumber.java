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

import static org.hamcrest.CoreMatchers.*;
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
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;

public class InvoiceTest_assignCollectionNumber {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    Numerators mockNumerators;
    
    @Ignoring
    @Mock
    DomainObjectContainer mockContainer;
    
    @Ignoring
    @Mock
    Property invoiceProperty;

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
        invoice.injectNumerators(mockNumerators);
        return invoice;
    }

    @Test
    public void happyCase_directDebit_and_collected_andWhenNoInvoiceNumberPreviouslyAssigned() {
        allowingMockNumeratorsRepoToReturn(numerator);

        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

        assertThat(invoice.hideAssignCollectionNumber(), is(false));
        assertThat(invoice.disableAssignCollectionNumber(), is(nullValue()));
        invoice.assignCollectionNumber();
        
        assertThat(invoice.getCollectionNumber(), is("XXX-00011"));
    }


    @Test
    public void whenInvoiceNumberAlreadyAssigned() {
        allowingMockNumeratorsRepoToReturn(numerator);

        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

        invoice.setCollectionNumber("SOME-COLLECTION-NUMBER");
        
        assertThat(invoice.hideAssignCollectionNumber(), is(false));
        assertThat(invoice.disableAssignCollectionNumber(), is("Collection number already assigned"));
        invoice.assignCollectionNumber();
        
        assertThat(invoice.getCollectionNumber(), is("SOME-COLLECTION-NUMBER"));
    }

    @Test
    public void whenNoProperty() {
        
        allowingMockNumeratorsRepoToReturn(null);
        
        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

        assertThat(invoice.hideAssignCollectionNumber(), is(false));
        assertThat(invoice.disableAssignCollectionNumber(), is("No COLLECTION_NUMBER numerator found for invoice's property"));
        
        invoice.assignCollectionNumber();
        assertThat(invoice.getCollectionNumber(), is(nullValue()));
    }

    @Test
    public void whenNotDirectDebit() {
        allowingMockNumeratorsRepoToReturn(numerator);
        
        invoice = createInvoice(invoiceProperty, PaymentMethod.BANK_TRANSFER, InvoiceStatus.APPROVED);
        
        assertThat(invoice.hideAssignCollectionNumber(), is(true));
        assertThat(invoice.disableAssignCollectionNumber(), is(nullValue()));
        
        invoice.assignCollectionNumber();
        
        assertThat(invoice.getCollectionNumber(), is(nullValue()));
    }
    
    @Test
    public void whenNotCollected() {
        allowingMockNumeratorsRepoToReturn(numerator);
        
        invoice = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.NEW);
        
        assertThat(invoice.hideAssignCollectionNumber(), is(false));
        assertThat(invoice.disableAssignCollectionNumber(), is("Must be in status of 'approved'"));
        
        invoice.assignCollectionNumber();
        
        assertThat(invoice.getCollectionNumber(), is(nullValue()));
    }
    
    


    private void allowingMockNumeratorsRepoToReturn(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockNumerators).findNumerator(NumeratorType.COLLECTION_NUMBER, invoiceProperty);
                will(returnValue(numerator));
            }
        });
    }
}
