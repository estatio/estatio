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
package org.estatio.module.capex.restapi;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.spi.DeriveBlobFromReturnedDocumentArg0;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.capex.app.DocumentBarcodeService;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(
        nature = NatureOfService.VIEW_REST_ONLY,
        objectType = "incodeDocuments.DocumentService"
)
public class DocumentServiceRestApi extends UdoDomainService<DocumentServiceRestApi> {

    public DocumentServiceRestApi() {
        super(DocumentServiceRestApi.class);
    }

    @Action(
            commandDtoProcessor = DeriveBlobFromReturnedDocumentArg0.class
    )
    public Document uploadGeneric(
            final Blob blob,
            final String documentType,
            final boolean barcodeInDocName,
            @Parameter(optionality = Optionality.OPTIONAL) String atPath) throws IllegalArgumentException {

        DocumentTypeData documentTypeData = DocumentTypeData.valueOf(documentType);
        final DocumentType type = documentTypeData.findUsing(documentTypeRepository);
        final String name = blob.getName();

        atPath = atPath != null ? atPath : "/FRA";

        // Could probably be done a little neater
        switch (atPath) {
            case "/ITA":
                if (documentTypeData == DocumentTypeData.INCOMING && barcodeInDocName)
                    return incomingDocumentRepository.upsert(type, atPath, name, blob);

                else if (documentTypeData == DocumentTypeData.TAX_REGISTER && !barcodeInDocName)
                    return incomingDocumentRepository.upsert(type, atPath, name, blob);

                else
                    throw new IllegalArgumentException(String.format("Combination documentType =  %s, barcodeInDocName = %s and atPath = %s is not supported", documentType, barcodeInDocName, atPath));

            case "/FRA":
                if (documentTypeData == DocumentTypeData.INCOMING && barcodeInDocName) {
                    Document result = incomingDocumentRepository.upsertAndArchive(type, documentBarcodeService.deriveAtPathFromBarcode(name), name, blob);
                    IncomingDocumentRepository.UploadDomainEvent event = new IncomingDocumentRepository.UploadDomainEvent();
                    event.setReturnValue(result);
                    event.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
                    eventBusService.post(event);

                    return result;
                } else {
                    throw new IllegalArgumentException(String.format("Combination documentType =  %s, barcodeInDocName = %s and atPath = %s is not supported", documentType, barcodeInDocName, atPath));
                }

            default:
                throw new IllegalArgumentException(String.format("Combination documentType =  %s, barcodeInDocName = %s and atPath = %s is not supported", documentType, barcodeInDocName, atPath));
        }
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    EventBusService eventBusService;

    @Inject
    DocumentBarcodeService documentBarcodeService;

}
