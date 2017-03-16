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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.paperclips.InvoiceDocAndCommService;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_downloadAbstract<T extends DocAndCommAbstract<T>>  {

    private final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;
    private final DocAndCommAbstract.Factory.DncProvider<T> provider;
    private final DocumentTypeData documentTypeData;

    public InvoiceSummaryForPropertyDueDateStatus_downloadAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final DocAndCommAbstract.Factory.DncProvider<T> provider,
            final DocumentTypeData documentTypeData) {
        this.invoiceSummary = invoiceSummary;
        this.provider = provider;
        this.documentTypeData = documentTypeData;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<T> act() {
        return findDocAndComms();
    }

    public String disableAct() {
        final List<T> docAndComms = findDocAndComms();
        for (T docAndComm : docAndComms) {
            final Document document = invoiceDocAndCommService.findDocument(docAndComm.getInvoice(), getDocumentType());
            if(document != null && document.getState() == DocumentState.RENDERED) {
                return null;
            }
        }
        return "No documents have been prepared & rendered";
    }

    DocumentType getDocumentType() {
        return documentTypeData.findUsing(documentTypeRepository, queryResultsCache);
    }


    private List<T> findDocAndComms() {
        final List<Invoice<?>> invoices = (List)invoiceSummary.getInvoices();
        return docAndCommFactory.documentsAndCommunicationsFor(invoices, provider);
    }

    @Inject
    DocAndCommForPrelimLetter.Factory docAndCommFactory;


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    InvoiceDocAndCommService invoiceDocAndCommService;

}
