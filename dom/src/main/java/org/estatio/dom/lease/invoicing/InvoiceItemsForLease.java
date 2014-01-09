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
package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.utils.StringUtils;
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
        InvoiceItemForLease invoiceItem = newTransientInstance();
        invoiceItem.setStartDate(interval.startDate());
        invoiceItem.setEndDate(interval.endDate());
        invoiceItem.setDueDate(dueDate);
        invoiceItem.modifyLeaseTerm(leaseTerm);
        persistIfNotAlready(invoiceItem);
        return invoiceItem;
    }

    // //////////////////////////////////////

    /**
     * 
     * @param leaseReference
     *            - not a <tt>Lease</tt>, because reference supports wildcards;
     *            there could be multiple leases to find.
     */
    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceItemForLease> findInvoiceItemsByLease(
            final String leaseReferenceOrName,
            final LocalDate startDate,
            final LocalDate dueDate) {
        return allMatches(
                "findByLeaseAndStartDateAndDueDate",
                "leaseReferenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(leaseReferenceOrName),
                "startDate", startDate,
                "dueDate", dueDate);
    }

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

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "99")
    public List<InvoiceItemForLease> allInvoiceItems() {
        return allInstances();
    }

}
