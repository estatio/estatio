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
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
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
        atPath = overrideUserAtPathUsingDocumentName(atPath, name);
        return incomingDocumentRepository.upsertAndArchive(type, atPath, name, blob);
    }

    String overrideUserAtPathUsingDocumentName(final String atPath, final String documentName){
        if (!isBarcode(documentName)) return atPath; // country prefix can be derived from barcodes only
        return deriveAtPathFromBarcode(documentName)!=null ? deriveAtPathFromBarcode(documentName) : atPath;
    }

    String deriveAtPathFromBarcode(final String documentName) {
        String countryPrefix = documentBarcodeService.countryPrefixFromBarcode(documentName);
        if (countryPrefix == null) return null;
        switch (countryPrefix) {
            case "FR":
                return "/FRA";
            case "BE":
                return "/BEL";
            case "IT":
                return "/ITA";
            default:
                return null;
        }
    }

    private boolean isBarcode(final String documentName) {
        return documentName.replace(".pdf", "").matches("\\d+");
    }



    // TODO: move this to a service I suppose ...
    @Action(
            commandDtoProcessor = DeriveBlobFromReturnedDocumentArg0.class  // TODO: What to do with this one? Assume not being picked up when programmatic ...
    )
    @Programmatic
    public Document uploadGeneric(final Blob blob, final String documentType, final boolean barcodeInDocName, @Parameter(optionality = Optionality.OPTIONAL) final String atPath) throws IllegalArgumentException {

        // implementation that supports barcode docs for incoming orders and invoices France and Belgium
        DocumentTypeData documentTypeData = DocumentTypeData.valueOf(documentType);
        final DocumentType type = documentTypeData.findUsing(documentTypeRepository);
        final String name = blob.getName();

        if (documentTypeData==DocumentTypeData.INCOMING && barcodeInDocName) {
            Document result =  incomingDocumentRepository.upsertAndArchive(type, deriveAtPathFromBarcode(name), name, blob);
            IncomingDocumentRepository.UploadDomainEvent event = new IncomingDocumentRepository.UploadDomainEvent();
            event.setReturnValue(result);
            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
            eventBusService.post(event);

            return result;
        }

        // implementation that supports order docs for Italy
        if (documentTypeData==DocumentTypeData.INCOMING_ORDER && !barcodeInDocName && atPath.startsWith("/ITA")){
            return incomingDocumentRepository.upsert(type, atPath, name, blob);
        }

        // implementation that supports order docs for Italy
        if (documentTypeData==DocumentTypeData.TAX_REGISTER && !barcodeInDocName && atPath.startsWith("/ITA")){
            return incomingDocumentRepository.upsert(type, atPath, name, blob);
        }

        throw new IllegalArgumentException(String.format("Combination documentType =  %s, barcodeInDocName = %s and atPath = %s is not supported", documentType, barcodeInDocName, atPath));

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
