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
package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.valuetypes.LocalDateInterval;

public class InvoiceItemsForLease extends EstatioDomainService<InvoiceItemForLease> {

    public InvoiceItemsForLease() {
        super(InvoiceItemsForLease.class, InvoiceItemForLease.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Programmatic
    public InvoiceItemForLease newInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval,
            final LocalDate dueDate) {
        Lease lease = leaseTerm.getLeaseItem().getLease();
        Invoice invoice = invoices.findOrCreateMatchingInvoice(
                leaseTerm.getLeaseItem().getPaymentMethod(),
                lease, 
                InvoiceStatus.NEW, 
                dueDate);
        InvoiceItemForLease invoiceItem = newTransientInstance();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setStartDate(interval.startDate());
        invoiceItem.setEndDate(interval.endDate());
        invoiceItem.setDueDate(dueDate);
        invoiceItem.modifyLeaseTerm(leaseTerm);
        persistIfNotAlready(invoiceItem);
        return invoiceItem;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceItemForLease> findByLeaseTermAndIntervalAndDueDateAndStatus(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval,
            final LocalDate dueDate,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseTermAndIntervalAndDueDateAndStatus",
                "leaseTerm", leaseTerm,
                "startDate", interval.startDate(),
                "endDate", interval.endDate(),
                "dueDate", dueDate,
                "invoiceStatus", invoiceStatus);
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceItemForLease> findByLeaseTermAndInterval(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval) {
        return allMatches(
                "findByLeaseTermAndInterval",
                "leaseTerm", leaseTerm,
                "startDate", interval.startDate(),
                "endDate", interval.endDate());
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceItemForLease> findByLeaseAndInvoiceStatus(
            final Lease lease,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseAndInvoiceStatus",
                "lease", lease,
                "invoiceStatus", invoiceStatus);
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceItemForLease> findByLeaseItemAndInvoiceStatus(
            final LeaseItem leaseItem,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseItemAndInvoiceStatus",
                "leaseItem", leaseItem,
                "invoiceStatus", invoiceStatus);
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceItemForLease> findByLeaseTermAndInvoiceStatus(
            final LeaseTerm leaseTerm,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseTermAndInvoiceStatus",
                "leaseTerm", leaseTerm,
                "invoiceStatus", invoiceStatus);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "99")
    public List<InvoiceItemForLease> allInvoiceItems() {
        return allInstances();
    }

    @Programmatic
    public InvoiceItemForLease findOrCreateUnapprovedInvoiceItemFor(
            final LeaseTerm leaseTerm,
            final LocalDateInterval invoiceInterval,
            final LocalDate dueDate) {
        InvoiceItemForLease ii = findUnapprovedInvoiceItemFor(leaseTerm, invoiceInterval, dueDate);
        if (ii == null) {
            ii = newInvoiceItem(leaseTerm, invoiceInterval, dueDate);
        }
        return ii;
    }

    @Programmatic
    public InvoiceItemForLease findUnapprovedInvoiceItemFor(
            final LeaseTerm leaseTerm,
            final LocalDateInterval invoiceInterval,
            final LocalDate dueDate) {

        List<InvoiceItemForLease> invoiceItems =
                findByLeaseTermAndIntervalAndDueDateAndStatus(
                        leaseTerm, invoiceInterval, dueDate, InvoiceStatus.NEW);
        if (invoiceItems.size() > 0) {
            // TODO: what should we do when we find more then one. Throw an
            // error?
            return invoiceItems.get(0);
        }
        return null;
    }

    @Programmatic
    public BigDecimal invoicedValueFor(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval) {
        BigDecimal invoicedValue = new BigDecimal(0);

        List<InvoiceItemForLease> items = findByLeaseTermAndInterval(leaseTerm, interval);
        for (InvoiceItemForLease invoiceItem : items) {
            Invoice invoice = invoiceItem.getInvoice();
            if (invoice.getStatus() != InvoiceStatus.NEW) {
                invoicedValue = invoicedValue.add(invoiceItem.getNetAmount());
            }
        }
        return invoicedValue;
    }

    @Programmatic
    public void removeUnapprovedInvoiceItemsForDate(LeaseTerm leaseTerm, LocalDateInterval interval) {
        List<InvoiceItemForLease> invoiceItems = findByLeaseTermAndInterval(leaseTerm, interval);
        for (InvoiceItemForLease invoiceItem : invoiceItems) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW))) {
                invoiceItem.remove();
            }
        }
        getContainer().flush();
    }

    // //////////////////////////////////////

    private Invoices invoices;

    public void injectInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }

}
