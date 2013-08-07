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

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

public class Invoices extends EstatioDomainService<Invoice> {

    public Invoices() {
        super(Invoices.class, Invoice.class);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @DescribedAs("New invoices, to be approved")
    @MemberOrder(sequence = "1")
    public List<Invoice> toBeApproved() {
        return allMatches("findByStatus", "status", InvoiceStatus.NEW);
    }

    @ActionSemantics(Of.SAFE)
    @DescribedAs("Approved invoices, to be collected")
    @MemberOrder(sequence = "2")
    public List<Invoice> toBeCollected() {
        return allMatches("findByStatus", "status", InvoiceStatus.APPROVED);
    }
    
    @ActionSemantics(Of.SAFE)
    @DescribedAs("Collected invoices, to be invoiced")
    @MemberOrder(sequence = "3")
    public List<Invoice> toBeInvoiced() {
        return allMatches("findByStatus", "status", InvoiceStatus.COLLECTED);
    }
    
    @ActionSemantics(Of.SAFE)
    @DescribedAs("Already invoiced")
    @MemberOrder(sequence = "4")
    public List<Invoice> previouslyInvoiced() {
        return allMatches("findByStatus", "status", InvoiceStatus.INVOICED);
    }
    
    

    // //////////////////////////////////////

    @Programmatic
    @ActionSemantics(Of.NON_IDEMPOTENT)
    public Invoice newInvoice() {
        Invoice invoice = newTransientInstance();
        persist(invoice);
        return invoice;
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceSource source, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        final List<Invoice> invoices = findMatchingInvoices(seller, buyer, paymentMethod, source, invoiceStatus, dueDate);
        return invoices.isEmpty() ? null : invoices.get(0);
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceSource source, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        return allMatches("findMatchingInvoices", "seller", seller, "buyer", buyer, "paymentMethod", paymentMethod, "source", source, "status", invoiceStatus, "dueDate", dueDate);
    }


    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "98")
    public List<Invoice> allInvoices() {
        return allInstances();
    }


}
