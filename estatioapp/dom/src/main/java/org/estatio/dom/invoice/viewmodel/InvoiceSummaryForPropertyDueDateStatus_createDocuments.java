
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
package org.estatio.dom.invoice.viewmodel;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.mixins.T_createDocumentAndRender;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoice_createDocumentAndScheduleRender;

@Mixin
public class InvoiceSummaryForPropertyDueDateStatus_createDocuments  {

    private final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;

    public InvoiceSummaryForPropertyDueDateStatus_createDocuments(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        this.invoiceSummary = invoiceSummary;
    }


    public InvoiceSummaryForPropertyDueDateStatus $$(final DocumentTemplate documentTemplate) throws IOException {

        final List<Invoice> invoices = invoiceSummary.getInvoices();
        for (Invoice invoice : invoices) {
            createDocumentMixin(invoice).$$(documentTemplate, T_createDocumentAndRender.Intent.CREATE_AND_ATTACH);
        }

        return this.invoiceSummary;
    }

    public String disable$$() {
        return invoiceSummary.getInvoices().isEmpty()? "No invoices": null;
    }

    public List<DocumentTemplate> choices0$$() {
        final List<Invoice> invoices = invoiceSummary.getInvoices();
        final Invoice anyInvoice = invoices.get(0);

        return Lists.newArrayList(
                createDocumentMixin(anyInvoice)
                        .choices0$$()
                        .stream()
                        .filter(x -> !x.isPreviewOnly())
                        .collect(Collectors.toList()));
    }

    Invoice_createDocumentAndScheduleRender createDocumentMixin(final Invoice anyInvoice) {
        return factoryService.mixin(Invoice_createDocumentAndScheduleRender.class, anyInvoice);
    }

    @Inject
    FactoryService factoryService;
}
