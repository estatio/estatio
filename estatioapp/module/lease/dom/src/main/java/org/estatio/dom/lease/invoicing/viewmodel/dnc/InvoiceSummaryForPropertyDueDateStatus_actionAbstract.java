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

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_actionAbstract {

    final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;
    final DocumentTypeData documentTypeData;

    public InvoiceSummaryForPropertyDueDateStatus_actionAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final DocumentTypeData documentTypeData) {
        this.invoiceSummary = invoiceSummary;
        this.documentTypeData = documentTypeData;
    }

    DocumentType getDocumentType() {
        return documentTypeData.findUsing(documentTypeRepository, queryResultsCache);
    }

    Document findMostRecentAttachedTo(final Invoice invoice, final DocumentType documentType) {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoice);
        for (Paperclip paperclip : paperclips) {
            final DocumentAbstract documentAbstract = paperclip.getDocument();
            if (!(documentAbstract instanceof Document)) {
                continue;
            }
            final Document document = (Document) documentAbstract;
            if(document.getType() == documentType) {
                return document;
            }
        }
        return null;
    }

    DocumentTemplate documentTemplateFor(final Invoice invoice) {
        final DocumentTemplate documentTemplate = queryResultsCache.execute(
                () -> documentTemplateRepository
                        .findFirstByTypeAndApplicableToAtPath(
                                getDocumentType(),
                                invoice.getApplicationTenancyPath()),
                InvoiceSummaryForPropertyDueDateStatus_actionAbstract.class,
                "documentTemplateFor",
                getDocumentType(), invoice
        );
        // best to fail fast...
        if(documentTemplate == null) {
            throw new ApplicationException(String.format(
                    "Could not locate a DocumentTemplate for type '%s' for invoice '%s'",
                    documentTypeData.getName(),
                    titleService.titleOf(invoice)));
        }
        return documentTemplate;
    }



    @Inject
    TitleService titleService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;



}
