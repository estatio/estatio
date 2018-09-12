/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.capex.dom.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.docs.QDocument;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.capex.dom.documents.IncomingDocumentRepository"
)
public class IncomingDocumentRepository extends DocumentRepository {

    @Programmatic
    public List<Document> findIncomingDocuments() {
        return queryResultsCache.execute(
                this::doFindIncomingDocuments,
                IncomingDocumentRepository.class,
                "findIncomingDocuments"
        );
    }

    private List<Document> doFindIncomingDocuments() {
        final List<Document> documents = findWithNoPaperclips();
        return documents.stream()
                .filter(document -> DocumentTypeData.docTypeDataFor(document).isIncoming())
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Document> findUnclassifiedIncomingOrders() {
        return queryResultsCache.execute(
                this::doFindUnclassifiedIncomingOrders,
                IncomingDocumentRepository.class,
                "findUnclassifiedIncomingOrders"
        );
    }

    private List<Document> doFindUnclassifiedIncomingOrders() {
        final List<Document> documents = findAttachedToExactlyOneFixedAssetOnly();
        return documents.stream()
                .filter(document -> DocumentTypeData.docTypeDataFor(document) == DocumentTypeData.INCOMING_ORDER)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Document> findUnclassifiedIncomingInvoices() {
        return queryResultsCache.execute(
                this::doFindUnclassifiedIncomingInvoices,
                IncomingDocumentRepository.class,
                "findUnclassifiedIncomingInvoices"
        );
    }

    private List<Document> doFindUnclassifiedIncomingInvoices() {
        final List<Document> documents = findAttachedToExactlyOneFixedAssetOnly();
        return documents.stream()
                .filter(document -> DocumentTypeData.docTypeDataFor(document) == DocumentTypeData.INCOMING_INVOICE)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Document> findAllIncomingDocumentsByName(final String name) {
        final QDocument q = QDocument.candidate();
        return isisJdoSupport.executeQuery(Document.class,
                q.name.eq(name)
        );
    }

    @Programmatic
    public List<Document> matchAllIncomingDocumentsByName(final String searchPhrase) {
        final QDocument q = QDocument.candidate();
        return isisJdoSupport.executeQuery(Document.class,
                q.name.toLowerCase().indexOf(searchPhrase.toLowerCase()).gt(-1)
        );
    }

    @Programmatic
    public List<Document> findAllIncomingDocuments() {
        final List<Document> documents = repositoryService.allInstances(Document.class);
        return documents.stream()
                .filter(document -> DocumentTypeData.docTypeDataFor(document).getNature() == DocumentTypeData.Nature.INCOMING)
                .collect(Collectors.toList());
    }

    // TODO: tackle this (and the filtering on DocumentType?) at db level
    private List<Document> findAttachedToExactlyOneFixedAssetOnly() {
        List<Document> result = new ArrayList<>();
        for (FixedAsset asset : propertyRepository.allProperties()){
            for (Paperclip paperclip : paperclipRepository.findByAttachedTo(asset)){
                Document document = (Document) paperclip.getDocument();
                if (paperclipRepository.findByDocument(document).size()==1) {
                    result.add(document);
                }
            }
        }
        return result;
    }

    public static class UploadDomainEvent extends ActionDomainEvent<Object> {}

    @Programmatic
    public Document upsertAndArchive(final DocumentType type, final String atPath, final String name, final Blob blob){
        synchronized (this) {
            Document document = null;
            final List<Document> incomingDocumentsWithSameName = findAllIncomingDocumentsByName(name);
            if (incomingDocumentsWithSameName.size()>0){
                document = incomingDocumentsWithSameName.get(0);
            }
            if (document!=null){
                if(Arrays.equals(document.getBlobBytes(), blob.getBytes())) {
                    return document;
                }
                // else...
                String prefix = "arch-".concat(clockService.nowAsLocalDateTime().toString("yyyy-MM-dd-HH-mm-ss")).concat("-");
                String archivedName = prefix.concat(document.getName());
                Document archivedDocument = documentService.createForBlob(document.getType(), document.getAtPath(), archivedName, document.getBlob());
                // update blobbytes of document
                document.setBlobBytes(blob.getBytes());
                // attach document to archived document
                paperclipRepository.attach(document, "", archivedDocument);
            } else {
                document = documentService.createForBlob(type, atPath, name, blob);
            }
            return document;
        }
    }

    @Programmatic
    public Document upsert(final DocumentType type, final String atPath, final String name, final Blob blob){
        synchronized (this) {
            Document document = null;
            final List<Document> incomingDocumentsWithSameName = findAllIncomingDocumentsByName(name);
            if (incomingDocumentsWithSameName.size()>0){
                document = incomingDocumentsWithSameName.get(0);
            }
            if (document==null) {
                document = documentService.createForBlob(type, atPath, name, blob);
            }
            return document;
        }
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    ClockService clockService;

    @Inject
    DocumentService documentService;

    @Inject
    IsisJdoSupport isisJdoSupport;

}
