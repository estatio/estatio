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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.google.common.io.BaseEncoding;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.canonical.documents.v2.DocumentNature;
import org.estatio.canonical.documents.v2.DocumentType;
import org.estatio.canonical.documents.v2.DocumentsDto;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;

@DomainService(
        nature = NatureOfService.VIEW_REST_ONLY,
        objectType = "incodeDocuments.DocumentService"
)
public class DocumentServiceRestApi {


    @Action(semantics = SemanticsOf.SAFE)
    public DocumentsDto findDocuments(
            @ParameterLayout(named = "invoiceNumber")
            final String invoiceNumber,
            @ParameterLayout(named = "year")
            final int year
    ) {
        final Optional<InvoiceForLease> invoiceIfAny =
                invoiceForLeaseRepository.findInvoiceByInvoiceNumber(invoiceNumber, year);
        final DocumentsDto documentsDto = new DocumentsDto();
        invoiceIfAny.ifPresent(
                invoiceForLease -> {
                    final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(invoiceForLease);
                    paperclips.stream()
                        .map(Paperclip::getDocument)
                        .filter(Document.class::isInstance)
                        .map(Document.class::cast)
                        .forEach(document -> {
                            final DocumentType documentDto = new DocumentType();
                            documentDto.setSelf(mappingHelper.oidDtoFor(document));
                            documentDto.setName(document.getName());
                            documentDto.setMimeType(document.getMimeType());
                            documentDto.setNature(natureOf(document));
                            switch (documentDto.getNature()) {
                            case BLOB:
                                final byte[] bytes = document.asBytes();
                                final String base64EncodedBytes = BaseEncoding.base64().encode(bytes);
                                documentDto.setBlobBytesBase64Encoded(base64EncodedBytes);
                                break;
                            case CLOB:
                                documentDto.setClobChars(document.asChars());
                                break;
                            }
                            documentsDto.getDocuments().add(documentDto);
                        });
                });
        return documentsDto;
    }

    private DocumentNature natureOf(final Document document) {
        if (document == null || document.getSort() == null) return null;
        return document.getSort().isBytes()
                ? DocumentNature.BLOB
                : DocumentNature.CLOB;
    }

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DtoMappingHelper mappingHelper;


}
