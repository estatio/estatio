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
package org.estatio.dom.lease.invoicing.viewmodel.dnc;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.invoicing.dnc.Invoice_createAndAttachDocumentAndScheduleRender;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.dom.lease.invoicing.InvoiceForLease;

public abstract class InvoiceSummaryForPropertyDueDateStatus_prepareAbstract extends InvoiceSummaryForPropertyDueDateStatus_actionAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_prepareAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final String documentTypeReference) {
        super(invoiceSummary, documentTypeReference);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public InvoiceSummaryForPropertyDueDateStatus $$() throws IOException {
        final List<InvoiceForLease> invoices = invoicesToPrepare();
        for (Invoice invoice : invoices) {
            final DocumentTemplate documentTemplate = documentTemplateFor(invoice);
            factoryService.mixin(Invoice_createAndAttachDocumentAndScheduleRender.class, invoice).$$(documentTemplate);
        }
        return this.invoiceSummary;
    }

    public String disable$$() {
        return invoicesToPrepare().isEmpty()? "No invoices available to be prepared": null;
    }

    private List<InvoiceForLease> invoicesToPrepare() {
        return FluentIterable.from(invoiceSummary.getInvoices()).filter(filter()).toList();
    }

    abstract Predicate<Invoice> filter();

    @Inject
    FactoryService factoryService;


}
