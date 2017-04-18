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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.schema.common.v1.OidDto;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "capex.IncomingInvoiceDocumentMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Incoming invoices"
)
public class IncomingInvoiceDocumentMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public IncomingInvoiceViewModel incomingDocuments() {
        final List<Document> documents = documentRepository.findWithNoPaperclips();
        final Integer idx = !documents.isEmpty() ? 0 : null;
        IncomingInvoiceViewModel viewModel = factory.create(documents, idx);

        return viewModel;
    }

    @Inject
    IncomingInvoiceViewModel.Factory factory;

    @Inject
    DocumentRepository documentRepository;

}
