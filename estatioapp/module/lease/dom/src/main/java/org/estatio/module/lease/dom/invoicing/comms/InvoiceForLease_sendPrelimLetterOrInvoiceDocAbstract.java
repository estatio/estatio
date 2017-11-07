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
package org.estatio.module.lease.dom.invoicing.comms;

import javax.inject.Inject;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.paperclips.InvoiceDocAndCommService;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

public abstract class InvoiceForLease_sendPrelimLetterOrInvoiceDocAbstract extends InvoiceForLease_sendAbstract {

    private final DocumentTypeData documentTypeData;

    public InvoiceForLease_sendPrelimLetterOrInvoiceDocAbstract(final InvoiceForLease invoice, final DocumentTypeData documentTypeData) {
        super(invoice);
        this.documentTypeData = documentTypeData;
    }

    DocumentType getDocumentType() {
        return documentTypeData.findUsing(documentTypeRepository, queryResultsCache);
    }

    Document findDocument() {
        final Document document = invoiceDocAndCommService.findDocument(invoice, getDocumentType());
        return document;
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;


}
