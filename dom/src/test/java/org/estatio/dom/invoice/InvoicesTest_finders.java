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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class InvoicesTest_finders {

    private FinderInteraction finderInteraction;

    private Invoices invoices;

    private Party seller;
    private Party buyer;
    private PaymentMethod paymentMethod;
    private InvoiceSource source;
    private InvoiceStatus invoiceStatus;
    private LocalDate dueDate;

    @Before
    public void setup() {
        
        seller = new PartyForTesting();
        buyer = new PartyForTesting();
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        source = new InvoiceSource(){};
        invoiceStatus = InvoiceStatus.APPROVED;
        dueDate = new LocalDate(2013,4,1);
        
        invoices = new Invoices() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<Invoice> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    @Test
    public void findMatchingInvoices() {

        invoices.findMatchingInvoices(seller, buyer, paymentMethod, source, invoiceStatus, dueDate);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Invoice.class));
        assertThat(finderInteraction.getQueryName(), is("findMatchingInvoices"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("buyer"), is((Object)buyer));
        assertThat(finderInteraction.getArgumentsByParameterName().get("seller"), is((Object)seller));
        assertThat(finderInteraction.getArgumentsByParameterName().get("paymentMethod"), is((Object)paymentMethod));
        assertThat(finderInteraction.getArgumentsByParameterName().get("source"), is((Object)source));
        assertThat(finderInteraction.getArgumentsByParameterName().get("status"), is((Object)invoiceStatus));
        assertThat(finderInteraction.getArgumentsByParameterName().get("dueDate"), is((Object)dueDate));

        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(6));
    }
    
    @Test
    public void findMatchingInvoice() {
        
        invoices.findMatchingInvoices(seller, buyer, paymentMethod, source, invoiceStatus, dueDate);
        
        // delegates to findMatchingInvoices, so this is correct...
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Invoice.class));
        assertThat(finderInteraction.getQueryName(), is("findMatchingInvoices"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("buyer"), is((Object)buyer));
        assertThat(finderInteraction.getArgumentsByParameterName().get("seller"), is((Object)seller));
        assertThat(finderInteraction.getArgumentsByParameterName().get("paymentMethod"), is((Object)paymentMethod));
        assertThat(finderInteraction.getArgumentsByParameterName().get("source"), is((Object)source));
        assertThat(finderInteraction.getArgumentsByParameterName().get("status"), is((Object)invoiceStatus));
        assertThat(finderInteraction.getArgumentsByParameterName().get("dueDate"), is((Object)dueDate));
        
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(6));
    }

    
    @Test
    public void findMatchingInvoice_whenMany_returnsFirst() {
        
        final Invoice invoice1 = new Invoice();
        final Invoice invoice2 = new Invoice();
        final Invoice invoice3 = new Invoice();
        
        invoices = new Invoices() {
            @Override
            @ActionSemantics(Of.SAFE)
            @Hidden
            public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceSource source, InvoiceStatus invoiceStatus, LocalDate dueDate) {
                return Arrays.asList(invoice1, invoice2, invoice3);
            }
        };
        
        assertThat(invoices.findMatchingInvoice(null, null, null, null, null, null), is(invoice1));
    }

    @Test
    public void findMatchingInvoice_whenEmpty_returnsNull() {
        
        invoices = new Invoices() {
            @Override
            @ActionSemantics(Of.SAFE)
            @Hidden
            public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceSource source, InvoiceStatus invoiceStatus, LocalDate dueDate) {
                return Arrays.<Invoice>asList();
            }
        };
        
        assertThat(invoices.findMatchingInvoice(null, null, null, null, null, null), is(nullValue()));
    }

    @Test
    public void allInvoices() {
        
        invoices.allInvoices();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
