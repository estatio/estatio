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
package org.incode.module.documents.dom.rendering;

import java.io.IOException;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.docs.Document;
import org.incode.module.documents.dom.docs.DocumentRepository;
import org.incode.module.documents.dom.docs.DocumentSort;
import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.types.DocumentType;

public abstract class RendererAbstract implements Renderer {

    @Override
    public Document render(
            final DocumentTemplate documentTemplate,
            final Object dataModel,
            final String documentName) {

        final DocumentType documentType = documentTemplate.getType();

        try {
            final DocumentSort sort = documentTemplate.getSort();
            final String mimeType = documentTemplate.getMimeType();
            final String atPath = documentTemplate.getAtPath();
            final LocalDateTime createdAt = clockService.nowAsLocalDateTime();

            switch (sort) {

                case BLOB:
                case EXTERNAL_BLOB:
                    final byte[] bytes = renderAsBytes(documentTemplate, dataModel, documentType);
                    final Blob blob = new Blob(documentName, mimeType, bytes);
                    return documentRepository.createBlob(documentType, atPath, blob, createdAt);

                case CLOB:
                case EXTERNAL_CLOB:
                    final String clobChars = renderAsChars(documentTemplate, dataModel, documentType);
                    final Clob clob = new Clob(documentName, mimeType, clobChars);
                    return documentRepository.createClob(documentType, atPath, clob, createdAt);

                case TEXT:
                    final String textChars = renderAsChars(documentTemplate, dataModel, documentType);

                    return documentRepository.createText(documentType, atPath, documentName, mimeType, textChars, createdAt);
            }
            return null;

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * Subclasses should override this method and/or {@link #renderAsBytes(DocumentTemplate, Object, DocumentType)}.
     */
    protected String renderAsChars(
            final DocumentTemplate documentTemplate,
            final Object dataModel,
            final DocumentType documentType) throws IOException {

        throw new IllegalStateException("This renderer cannot render to characters");
    }

    /**
     * Subclasses should override this method and/or {@link #renderAsChars(DocumentTemplate, Object, DocumentType)}.
     */
    protected byte[] renderAsBytes(
            final DocumentTemplate documentTemplate,
            final Object dataModel,
            final DocumentType documentType) throws IOException {

        throw new IllegalStateException("This renderer cannot render to bytes");
    }

    @Inject
    private DocumentRepository documentRepository;
    @Inject
    private ClockService clockService;

}
