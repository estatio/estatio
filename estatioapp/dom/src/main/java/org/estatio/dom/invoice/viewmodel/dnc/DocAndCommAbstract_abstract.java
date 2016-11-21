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

import javax.inject.Inject;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.paperclips.InvoiceDocAndCommService;

public abstract class DocAndCommAbstract_abstract<T extends DocAndCommAbstract<T>> {

    final T docAndComm;
    private final String documentTypeReference;

    public DocAndCommAbstract_abstract(final T docAndComm, final String documentTypeReference) {
        this.docAndComm = docAndComm;
        this.documentTypeReference = documentTypeReference;
    }

    Invoice getInvoice() {
        return docAndComm.getInvoice();
    }

    DocumentType getDocumentType() {
        return queryResultsCache.execute(
                () -> documentTypeRepository.findByReference(documentTypeReference),
                DocAndCommAbstract_abstract.class,
                "getDocumentType", documentTypeReference);
    }

    Document getDocument() {
        return invoiceDocAndCommService.findDocument(getInvoice(), getDocumentType());
    }

    Communication getCommunication() {
        return invoiceDocAndCommService.findCommunication(getInvoice(), getDocumentType());
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;
}
