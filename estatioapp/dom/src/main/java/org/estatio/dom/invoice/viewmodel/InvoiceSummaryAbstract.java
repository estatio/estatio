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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

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

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Object collectAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.doCollect();
        }
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object invoiceAll(final LocalDate invoiceDate) {
        for (Invoice invoice : getInvoices()) {
            invoice.doInvoice(invoiceDate);
        }
        return this;
    }

    public LocalDate default0InvoiceAll() {
        return getClockService().now();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object removeAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.remove();
        }
        return this;
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object zapAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.remove();
        }
        return this;
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryAbstract saveAllAsHistoric() {
        for (Invoice invoice : getInvoices()) {
            invoice.saveAsHistoric();
        }
        return this;
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    public abstract List<Invoice> getInvoices();

    @Inject
    protected Invoices invoiceRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

}
