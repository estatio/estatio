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
package org.estatio.dom.invoice.viewmodel.dnc;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationState;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

@Mixin
public class InvoiceSummaryForPropertyDueDateStatus_downloadforSending {

    public static final String MIME_TYPE_APPLICATION_PDF = "application/pdf";
    private final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;

    public InvoiceSummaryForPropertyDueDateStatus_downloadforSending(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        this.invoiceSummary = invoiceSummary;
    }

    public enum Sort {
        NOT_YET_SENT,
        PENDING_AND_SENT
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Object $$(
            final DocumentTemplate documentTemplate,
            final String fileName,
            final Sort sort) throws IOException {

        final List<Invoice> invoices = invoiceSummary.getInvoices();

        final List<byte[]> pdfBytes = Lists.newArrayList();

        for (Invoice invoice: invoices) {
            final Communication communication = invoiceDocAndCommService
                    .findCommunication(invoice, documentTemplate.getType());
            if (communication == null) {
                continue;
            }

            if(sort == Sort.NOT_YET_SENT && communication.getState() == CommunicationState.SENT) {
                continue;
            }

            final Document enclosed = communication.findDocumentIfAny(DocumentConstants.PAPERCLIP_ROLE_ENCLOSED);
            if (enclosed == null) {
                continue;
            }

            if(enclosed.getState() == DocumentState.NOT_RENDERED) {
                continue;
            }
            final String mimeType = enclosed.getMimeType();
            if (!MIME_TYPE_APPLICATION_PDF.equals(mimeType)) {
                continue;
            }

            final DocumentSort documentSort = enclosed.getSort();
            switch (documentSort) {
                case BLOB:
                case EXTERNAL_BLOB:
                    final byte[] bytes = documentSort.asBytes(enclosed);
                    pdfBytes.add(bytes);
                    communication.sent();
                    break;
            }
        }

        if(pdfBytes.isEmpty()) {
            messageService.warnUser("No documents to be merged");
            return invoiceSummary;
        }

        final byte[] mergedBytes = pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));

        return new Blob(fileName, MIME_TYPE_APPLICATION_PDF, mergedBytes);
    }

    public String disable$$() {
        return invoiceSummary.getInvoices().isEmpty()? "No invoices": null;
    }

    public List<DocumentTemplate> choices0$$() {
        final List<Invoice> invoices = invoiceSummary.getInvoices();
        final Invoice anyInvoice = invoices.get(0);
        return invoiceDocumentTemplateService.templatesFor(anyInvoice);
    }

    public String default1$$() {
        return "merged.pdf";
    }

    public Sort default2$$() {
        return Sort.NOT_YET_SENT;
    }

    @Inject
    InvoiceDocumentTemplateService invoiceDocumentTemplateService;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;

    @Inject
    PdfBoxService pdfBoxService;

    @Inject
    MessageService messageService;

}
