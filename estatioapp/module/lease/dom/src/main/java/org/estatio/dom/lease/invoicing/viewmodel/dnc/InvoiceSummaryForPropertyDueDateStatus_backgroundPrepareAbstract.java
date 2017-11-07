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

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.paperclips.InvoiceDocAndCommService;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.dnc.InvoiceForLease_backgroundPrepare;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_backgroundPrepareAbstract extends InvoiceSummaryForPropertyDueDateStatus_actionAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_backgroundPrepareAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final DocumentTypeData documentTypeData) {
        super(invoiceSummary, documentTypeData);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public InvoiceSummaryForPropertyDueDateStatus $$() throws IOException {
        final List<InvoiceForLease> invoices = invoicesToPrepare();
        for (Invoice invoice : invoices) {
            final DocumentTemplate documentTemplate = documentTemplateFor(invoice);
            factoryService.mixin(InvoiceForLease_backgroundPrepare.class, invoice).$$(documentTemplate);
        }
        return this.invoiceSummary;
    }

    public String disable$$() {
        return invoicesToPrepare().isEmpty()? "No invoices available to be prepared": null;
    }

    private List<InvoiceForLease> invoicesToPrepare() {
        final List<InvoiceForLease> invoices = invoiceSummary.getInvoices();
        return FluentIterable.from(invoices)
                .filter(invoice -> noDocumentOrNotYetSent(invoice))
                .filter(filter())
                .toList();
    }

    private boolean noDocumentOrNotYetSent(final InvoiceForLease invoice) {

        final Document document = findMostRecentAttachedTo(invoice, getDocumentType());
        if(document == null) {
            return true;
        }
        if(document.getState() == DocumentState.NOT_RENDERED) {
            return true;
        }

        return invoiceDocAndCommService.findFirstCommunication(document) == null;
    }

    abstract Predicate<Invoice> filter();

    @Inject
    FactoryService factoryService;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;


}
