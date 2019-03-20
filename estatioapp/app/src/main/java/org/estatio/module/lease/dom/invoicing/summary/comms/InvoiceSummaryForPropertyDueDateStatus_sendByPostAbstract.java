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
package org.estatio.module.lease.dom.invoicing.summary.comms;

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

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.comms.InvoiceForLease_sendByPost;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;

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

            final InvoiceForLease_sendByPost invoice_sendByPost = invoice_sendByPost(invoice);
            final PostalAddress postalAddress = invoice_sendByPost.default1$$(prelimLetterOrInvoiceNote);

            invoice_sendByPost.createPostalCommunicationAsSent(prelimLetterOrInvoiceNote, postalAddress);
            invoice_sendByPost.appendPdfBytes(prelimLetterOrInvoiceNote, pdfBytes); // TODO: EST-1807 his seems method not to pick up supporting documents
        }

        final byte[] mergedBytes = pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));

        return new Blob(fileName, MimeTypes.APPLICATION_PDF.asStr(), mergedBytes);
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
            final InvoiceForLease_sendByPost invoice_sendByPost = invoice_sendByPost(invoiceAndDocument.getInvoice());
            final PostalAddress postalAddress = invoice_sendByPost.default1$$(invoiceAndDocument.getDocument());
            return postalAddress != null;
        };
    }

    private InvoiceForLease_sendByPost invoice_sendByPost(final Invoice invoice) {
        return factoryService.mixin(InvoiceForLease_sendByPost.class, invoice);
    }


    @Inject
    PdfBoxService pdfBoxService;

    @Inject
    FactoryService factoryService;


}
