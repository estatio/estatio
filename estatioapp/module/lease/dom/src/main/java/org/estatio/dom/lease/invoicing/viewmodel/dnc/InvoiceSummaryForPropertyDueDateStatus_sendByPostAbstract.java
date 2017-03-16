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
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.invoicing.dnc.Invoice_sendByPost;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_sendByPostAbstract extends InvoiceSummaryForPropertyDueDateStatus_sendAbstract {

    private final String defaultFileName;

    public InvoiceSummaryForPropertyDueDateStatus_sendByPostAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final DocumentTypeData documentTypeData) {
        super(invoiceSummary, documentTypeData, CommunicationChannelType.POSTAL_ADDRESS);
        this.defaultFileName = documentTypeData.getMergedFileName();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob $$(final String fileName) throws IOException {

        final List<byte[]> pdfBytes = Lists.newArrayList();

        for (final InvoiceAndDocument invoiceAndDocument : invoiceAndDocumentsToSend()) {

            final Invoice invoice = invoiceAndDocument.getInvoice();
            final Document prelimLetterOrInvoiceNote = invoiceAndDocument.getDocument();

            final Invoice_sendByPost invoice_sendByPost = invoice_sendByPost(invoice);
            final PostalAddress postalAddress = invoice_sendByPost.default1$$(prelimLetterOrInvoiceNote);

            invoice_sendByPost.createPostalCommunicationAsSent(prelimLetterOrInvoiceNote, postalAddress);
            invoice_sendByPost.appendPdfBytes(prelimLetterOrInvoiceNote, pdfBytes);
        }

        final byte[] mergedBytes = pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));

        return new Blob(fileName, DocumentConstants.MIME_TYPE_APPLICATION_PDF, mergedBytes);
    }


    public String disable$$() {
        return invoiceAndDocumentsToSend().isEmpty()? "No documents available to be send by post": null;
    }

    public String default0$$() {
        return  defaultFileName;
    }


    @Override
    Predicate<InvoiceAndDocument> filter() {
        return Predicates.and(InvoiceAndDocument.Predicates.isDocPdfAndBlob(), withPostalAddress());
    }

    private Predicate<InvoiceAndDocument> withPostalAddress() {
        return invoiceAndDocument -> {
            final Invoice_sendByPost invoice_sendByPost = invoice_sendByPost(invoiceAndDocument.getInvoice());
            final PostalAddress postalAddress = invoice_sendByPost.default1$$(invoiceAndDocument.getDocument());
            return postalAddress != null;
        };
    }

    private Invoice_sendByPost invoice_sendByPost(final Invoice invoice) {
        return factoryService.mixin(Invoice_sendByPost.class, invoice);
    }


    @Inject
    PdfBoxService pdfBoxService;

    @Inject
    FactoryService factoryService;


}
