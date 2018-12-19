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
package org.estatio.module.lease.contentmapping;

import java.util.List;

import com.google.common.io.BaseEncoding;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.canonical.documents.v2.DocumentNature;
import org.estatio.canonical.documents.v2.DocumentType;
import org.estatio.canonical.documents.v2.DocumentsDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentsDtoFactory extends DtoFactoryAbstract<List, DocumentsDto> {

    public DocumentsDtoFactory() {
        super(List.class, DocumentsDto.class);
    }

    @Override
    protected DocumentsDto newDto(final List documents) {
        return internalNewDto(documents);
    }

    DocumentsDto internalNewDto(final List<Document> documents) {
        final DocumentsDto documentsDto = new DocumentsDto();
        documents.forEach(document -> documentsDto.getDocuments().add(newDto(document)));
        return documentsDto;
    }

    private DocumentType newDto(final Document document) {
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
        return documentDto;
    }

    private DocumentNature natureOf(final Document document) {
        if (document == null || document.getSort() == null) return null;
        return document.getSort().isBytes()
                ? DocumentNature.BLOB
                : DocumentNature.CLOB;
    }

}
