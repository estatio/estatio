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
package org.estatio.capex.dom.documents;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.invoice.DocumentTypeData;

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
        return Lists.newArrayList(
                FluentIterable.from(documents)
                .filter(document -> DocumentTypeData.docTypeDataFor(document).isIncoming())
                .toList()
        );
    }

    @Programmatic
    public List<Document> findIncomingOrders() {
        return queryResultsCache.execute(
                this::doFindIncomingOrders,
                IncomingDocumentRepository.class,
                "findIncomingOrders"
        );
    }

    private List<Document> doFindIncomingOrders() {
        final List<Document> documents = findAttachedToFixedAsset();
        return Lists.newArrayList(
                FluentIterable.from(documents)
                        .filter(document -> DocumentTypeData.docTypeDataFor(document)==DocumentTypeData.INCOMING_ORDER)
                        .toList()
        );
    }

    @Programmatic
    public List<Document> findIncomingInvoices() {
        return queryResultsCache.execute(
                this::doFindIncomingInvoices,
                IncomingDocumentRepository.class,
                "findIncomingInvoices"
        );
    }

    private List<Document> doFindIncomingInvoices() {
        final List<Document> documents = findAttachedToFixedAsset();
        return Lists.newArrayList(
                FluentIterable.from(documents)
                        .filter(document -> DocumentTypeData.docTypeDataFor(document)==DocumentTypeData.INCOMING_INVOICE)
                        .toList()
        );
    }

    // TODO: tackle this (and the filtering on DocumentType?) at db level
    private List<Document> findAttachedToFixedAsset() {
        List<Document> result = new ArrayList<>();
        for (FixedAsset asset : propertyRepository.allProperties()){
            for (Paperclip paperclip : paperclipRepository.findByAttachedTo(asset)){
                result.add((Document) paperclip.getDocument());
            }
        }
        return result;
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    PropertyRepository propertyRepository;

}
