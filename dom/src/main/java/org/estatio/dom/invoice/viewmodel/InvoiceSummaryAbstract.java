/*
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
package org.estatio.dom.invoice.viewmodel;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoices;

public abstract class InvoiceSummaryAbstract extends EstatioViewModel {

    public Object approveAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.doApprove();
        }
        return this;
    }

    public Object collectAll(
            final @Named("Are you sure?") Boolean confirm
            ) {
        for (Invoice invoice : getInvoices()) {
            invoice.doCollect();
        }
        return this;
    }

    public Object invoiceAll(
            final @Named("Invoice Date") LocalDate invoiceDate,
            final @Named("Are you sure?") Boolean confirm
            ) {
        for (Invoice invoice : getInvoices()) {
            invoice.doInvoice(invoiceDate);
        }
        return this;
    }

    public LocalDate default0InvoiceAll() {
        return getClockService().now();
    }

    public Object removeAll(final @Named("Confirm") Boolean confirm) {
        for (Invoice invoice : getInvoices()) {
            invoice.remove();
        }
        return this;
    }

    @Prototype
    public Object zapAll(final @Named("Confirm") Boolean confirm) {
        for (Invoice invoice : getInvoices()) {
            invoice.doRemove();
        }
        return this;
    }

    @Render(Type.EAGERLY)
    public abstract List<Invoice> getInvoices();

    // //////////////////////////////////////

    protected Invoices invoicesService;

    final public void injectInvoicesService(final Invoices invoicesService) {
        this.invoicesService = invoicesService;
    }

}
