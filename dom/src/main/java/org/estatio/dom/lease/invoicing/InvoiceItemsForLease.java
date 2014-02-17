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

import org.apache.isis.applib.ApplicationException;
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
            final LocalDate dueDate, 
            final String interactionId) {
        Lease lease = leaseTerm.getLeaseItem().getLease();
        Invoice invoice = invoices.findOrCreateMatchingInvoice(
                leaseTerm.getLeaseItem().getPaymentMethod(),
                lease,
                InvoiceStatus.NEW,
                dueDate,
                interactionId);
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
    public List<InvoiceItemForLease> findByLeaseTermAndIntervalAndInvoiceStatus(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval,
            final InvoiceStatus invoiceStatus) {
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
    public InvoiceItemForLease createUnapprovedInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDateInterval invoiceInterval,
            final LocalDate dueDate, 
            final String interactionId) {
        // TODO:Removing items returns unwanted results, perhaps remove all old
        // runs before?
        // removeUnapprovedInvoiceItems(leaseTerm, invoiceInterval);
        return newInvoiceItem(leaseTerm, invoiceInterval, dueDate, interactionId);
    }

    @Programmatic
    public InvoiceItemForLease findUnapprovedInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDateInterval invoiceInterval) {

        List<InvoiceItemForLease> invoiceItems =
                findByLeaseTermAndIntervalAndInvoiceStatus(
                        leaseTerm, invoiceInterval, InvoiceStatus.NEW);
        if (invoiceItems.size() > 1) {
            throw new ApplicationException("Found more then one unapproved invoice items for" + leaseTerm.toString() + invoiceInterval.toString());
        }
        if (invoiceItems.size() == 1) {
            return invoiceItems.get(0);
        }
        return null;
    }

    @Programmatic
    public BigDecimal invoicedValue(
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
    public void removeUnapprovedInvoiceItems(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval) {
        List<InvoiceItemForLease> invoiceItems = findByLeaseTermAndIntervalAndInvoiceStatus(
                leaseTerm,
                interval,
                InvoiceStatus.NEW);
        for (InvoiceItemForLease invoiceItem : invoiceItems) {
            invoiceItem.remove();
        }
        getContainer().flush();
    }

    // //////////////////////////////////////

    private Invoices invoices;

    public void injectInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }

}
