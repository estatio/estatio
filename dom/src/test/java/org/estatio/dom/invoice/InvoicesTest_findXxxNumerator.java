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

import java.math.BigInteger;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.asset.Property;
import org.estatio.dom.numerator.Numerators;

public class InvoicesTest_findXxxNumerator {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Numerators mockNumerators;
    
    Invoices invoices;
    
    @Ignoring
    @Mock
    Property mockProperty;

    private String format;
    private BigInteger lastIncrement;

    @Before
    public void setUp() throws Exception {
        format = "0%6d";
        lastIncrement = BigInteger.TEN;
        
        invoices = new Invoices();
        invoices.injectNumerators(mockNumerators);
    }


    @Test
    public void findCollectionNumberNumerator() {
        context.checking(new Expectations() {
            {
                oneOf(mockNumerators).findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
            }
        });
        invoices.findCollectionNumberNumerator();
    }

    @Test
    public void createCollectionNumberNumerator() {
        context.checking(new Expectations() {
            {
                oneOf(mockNumerators).createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, format, lastIncrement);
            }
        });
        invoices.createCollectionNumberNumerator(format, lastIncrement);
    }

    @Hidden
    public void findInvoiceNumberNumerator() {
        context.checking(new Expectations() {
            {
                oneOf(mockNumerators).createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, format, lastIncrement);
            }
        });
        invoices.findInvoiceNumberNumerator(mockProperty);
    }

    @Hidden
    public void createInvoiceNumberNumerator(
            final Property property,
            final String format,
            final BigInteger lastIncrement) {
        
        context.checking(new Expectations() {
            {
                oneOf(mockNumerators).createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, mockProperty, format, lastIncrement);
            }
        });
        invoices.createInvoiceNumberNumerator(mockProperty, format, lastIncrement);
    }
    
    
    
}
