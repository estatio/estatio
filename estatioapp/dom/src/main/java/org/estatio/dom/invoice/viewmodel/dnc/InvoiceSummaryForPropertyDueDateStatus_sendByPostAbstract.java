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
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.mixins.Document_print;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;

import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_sendByPostAbstract extends InvoiceSummaryForPropertyDueDateStatus_sendAbstract {

    static final String MIME_TYPE_APPLICATION_PDF = "application/pdf";
    private final String defaultFileName;

    public InvoiceSummaryForPropertyDueDateStatus_sendByPostAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final String documentTypeReference,
            final String defaultFileName) {
        super(invoiceSummary, documentTypeReference, CommunicationChannelType.POSTAL_ADDRESS);
        this.defaultFileName = defaultFileName;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob $$(final String fileName) throws IOException {

        final List<byte[]> pdfBytes = Lists.newArrayList();

        for (Document document : documentsToSend()) {

            final Document_print printMixin = printMixin(document);
            final PostalAddress postalAddress = printMixin.default0$$();

            final DocumentSort documentSort = document.getSort();
            final byte[] bytes = documentSort.asBytes(document);
            pdfBytes.add(bytes);

            final Communication communication = printMixin.$$(postalAddress);
            communication.sent();
        }

        final byte[] mergedBytes = pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));

        return new Blob(fileName, MIME_TYPE_APPLICATION_PDF, mergedBytes);
    }


    public String disable$$() {
        return documentsToSend().isEmpty()? "No documents available to be send by post": null;
    }

    public String default0$$() {
        return  defaultFileName;
    }

    @Override
    List<Document> documentsToSend() {
        return documentsToSend(canBeSentByPost());
    }

    private Predicate<Document> canBeSentByPost() {
        return Predicates.and(isPdfAndBlob(), withPostalAddress());
    }
    private Predicate<Document> withPostalAddress() {
        return document -> {
            final Document_print printMixin = printMixin(document);
            final PostalAddress postalAddress = printMixin.default0$$();
            return postalAddress != null;
        };
    }

    static Predicate<Document> isPdfAndBlob() {
        return Predicates.and(isPdf(), isBlobSort());
    }

    static Predicate<Document> isPdf() {
        return document -> MIME_TYPE_APPLICATION_PDF.equals(document.getMimeType());
    }

    static Predicate<Document> isBlobSort() {
        return document -> {
            final DocumentSort documentSort = document.getSort();
            return !(documentSort != DocumentSort.BLOB && documentSort != DocumentSort.EXTERNAL_BLOB);
        };
    }

    private Document_print printMixin(final Document document) {
        return factoryService.mixin(Document_print.class, document);
    }


    @Inject
    PdfBoxService pdfBoxService;

    @Inject
    FactoryService factoryService;


}
