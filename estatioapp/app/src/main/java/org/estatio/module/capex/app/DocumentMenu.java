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
package org.estatio.module.capex.app;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.spi.DeriveBlobFromReturnedDocumentArg0;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incodeDocuments.DocumentMenu"
)
@DomainServiceLayout(
        named = "Tasks & Docs",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "75.1")
public class DocumentMenu extends UdoDomainService<DocumentMenu> {

    public DocumentMenu() {
        super(DocumentMenu.class);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<Document> findDocuments(
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate
    ) {
        return documentRepository.findBetween(startDate, endDate);
    }

    public LocalDate default0FindDocuments() {
        // one week ago.
        return clockService.now().plusDays(-7);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Document> matchIncomingDocumentsByName(@MinLength(3) final String nameOrBarcode){
        return incomingDocumentRepository.matchAllIncomingDocumentsByName(nameOrBarcode);
    }

    @Action(domainEvent = IncomingDocumentRepository.UploadDomainEvent.class,
            commandDtoProcessor = DeriveBlobFromReturnedDocumentArg0.class
    )
    @MemberOrder(sequence = "3")
    public Document upload(final Blob blob) {
        final String name = blob.getName();
        final DocumentType type = DocumentTypeData.INCOMING.findUsing(documentTypeRepository);
        final ApplicationUser me = meService.me();
        String atPath = me != null ? me.getFirstAtPathUsingSeparator(';') : null;
        if (atPath == null) {
            atPath = "/";
        }
        atPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(atPath, name);
        return incomingDocumentRepository.upsertAndArchive(type, atPath, name, blob);
    }

    @Inject
    MeService meService;

    @Inject
    ClockService clockService;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    DocumentBarcodeService documentBarcodeService;

    @Inject
    EventBusService eventBusService;

}
