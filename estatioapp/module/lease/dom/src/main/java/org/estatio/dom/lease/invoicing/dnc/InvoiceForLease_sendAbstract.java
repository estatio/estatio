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
package org.estatio.dom.lease.invoicing.dnc;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.communications.dom.mixins.Document_sendByEmail;
import org.incode.module.communications.dom.mixins.Document_sendByPost;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.lease.invoicing.InvoiceForLease;

public abstract class InvoiceForLease_sendAbstract {

    final InvoiceForLease invoice;

    public InvoiceForLease_sendAbstract(final InvoiceForLease invoice) {
        this.invoice = invoice;
    }

    Communication createEmailCommunication(
            final Document document,
            final EmailAddress toChannel,
            final String cc,
            final String cc2,
            final String cc3,
            final String bcc,
            final String bcc2
            )
            throws IOException {

        // just delegate to Document_sendByEmail to do the work, then attach to buyer and seller
        final Communication communication = document_sendByEmail(document).act(toChannel, cc, cc2, cc3, bcc, bcc2);
        attachToBuyerAndSeller(document);

        return communication;
    }

    @Programmatic
    public Communication createPostalCommunicationAsSent(
            final Document document,
            @ParameterLayout(named = "to:")
            final PostalAddress toChannel) throws IOException {

        // just delegate to document_sendByPost to do the work, then attach to buyer and seller
        final Communication communication = document_sendByPost(document).act(toChannel);
        attachToBuyerAndSeller(document);

        // and mark as sent
        communication.sent();

        return communication;
    }

    Document_sendByEmail document_sendByEmail(final Document document) {
        return factoryService.mixin(Document_sendByEmail.class, document);
    }

    Document_sendByPost document_sendByPost(final Document document) {
        return factoryService.mixin(Document_sendByPost.class, document);
    }

    private void attachToBuyerAndSeller(final Document document) {
        paperclipRepository.attach(document, PaperclipRoleNames.INVOICE_BUYER, invoice.getBuyer());
        paperclipRepository.attach(document, PaperclipRoleNames.INVOICE_SELLER, invoice.getSeller());
    }


    byte[] mergePdfBytes(final Document document) throws IOException {
        final List<byte[]> pdfBytes = Lists.newArrayList();
        appendPdfBytes(document, pdfBytes);
        return pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));
    }

    @Programmatic
    public void appendPdfBytes(final Document prelimLetterOrInvoiceDoc, final List<byte[]> pdfBytes) {

        // this one should be a PDF
        appendBytesIfPdf(prelimLetterOrInvoiceDoc, pdfBytes);

        // and any attachments that are PDFs are also merged in
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(prelimLetterOrInvoiceDoc);
        for (Paperclip paperclip : paperclips) {
            final Object objAttachedToDocument = paperclip.getAttachedTo();
            if(objAttachedToDocument instanceof Document) {
                final Document docAttachedToDocument = (Document) objAttachedToDocument;
                appendBytesIfPdf(docAttachedToDocument, pdfBytes);
            }
        }
    }


    private static void appendBytesIfPdf(final Document docAttachedToDocument, final List<byte[]> pdfBytes) {
        final DocumentSort attachedDocSort = docAttachedToDocument.getSort();
        if (!attachedDocSort.isBytes()) {
            return;
        }
        final String mimeType = docAttachedToDocument.getMimeType();
        if (!DocumentConstants.MIME_TYPE_APPLICATION_PDF.equals(mimeType)) {
            return;
        }
        final byte[] attachedDocBytes = attachedDocSort.asBytes(docAttachedToDocument);
        pdfBytes.add(attachedDocBytes);
    }



    @Inject
    FactoryService factoryService;

    @Inject
    PaperclipRepository paperclipRepository;


    @Inject
    PdfBoxService pdfBoxService;

}
