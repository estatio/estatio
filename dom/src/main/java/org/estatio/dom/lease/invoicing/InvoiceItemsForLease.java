/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.utils.StringUtils;

public class InvoiceItemsForLease extends EstatioDomainService<InvoiceItemForLease> {

    public InvoiceItemsForLease() {
        super(InvoiceItemsForLease.class, InvoiceItemForLease.class);
    }
    
    // //////////////////////////////////////

    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public InvoiceItemForLease newInvoiceItem() {
        InvoiceItemForLease invoiceItem = newTransientInstance();
        persist(invoiceItem);
        return invoiceItem;
    }


    // //////////////////////////////////////

    /**
     * 
     * @param leaseReference - not a {@link Lease}, because reference supports wildcards; there could be multiple leases to find.
     */
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Invoices", sequence="2")
    public List<InvoiceItemForLease> findInvoiceItemsByLease(
            final @Named("Lease reference") String leaseReference, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("Due Date") LocalDate dueDate) {
        return allMatches("findByLeaseAndStartDateAndDueDate", "leaseReference", StringUtils.wildcardToRegex(leaseReference), "startDate", startDate, "dueDate", dueDate);
    }

    
    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Invoices", sequence = "99")
    public List<InvoiceItemForLease> allInvoiceItems() {
        return allInstances();
    }

}
