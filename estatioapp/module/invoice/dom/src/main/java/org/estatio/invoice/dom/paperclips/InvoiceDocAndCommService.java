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
package org.estatio.invoice.dom.paperclips;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.estatio.invoice.dom.Invoice;

@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceDocAndCommService {

    @Programmatic
    public Document findDocument(final Invoice invoice, final DocumentType documentType) {
        return queryResultsCache
                .execute(() -> findDocumentNotCached(invoice, documentType),
                        InvoiceDocAndCommService.class,
                        "findDocument",
                        invoice,
                        documentType);
    }


    private Document findDocumentNotCached(final Invoice invoice, final DocumentType documentType) {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoice);
        return (Document) paperclips
                .stream()
                .map(paperclip -> paperclip.getDocument())
                .filter(document -> document.getType() == documentType)
                .filter(Document.class::isInstance)
                .findFirst()
                .orElse(null);
    }

    @Programmatic
    public Communication findCommunication(final Invoice invoice, final DocumentType documentType) {
        return queryResultsCache
                .execute(() -> findCommunicationNotCached(invoice, documentType),
                        InvoiceDocAndCommService.class,
                        "findCommunication",
                        invoice,
                        documentType);
    }

    private Communication findCommunicationNotCached(final Invoice invoice, final DocumentType documentType) {
        final Document document = findDocument(invoice, documentType);
        if (document == null) {
            return null;
        }
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        return (Communication) paperclips
                .stream()
                .map(paperclip -> paperclip.getAttachedTo())
                .filter(attachedTo -> {
                    return Communication.class.isInstance(attachedTo);
                })
                .findFirst()
                .orElse(null);
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    QueryResultsCache queryResultsCache;

}
