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
package org.estatio.app.integration.documents;

import java.io.IOException;

import javax.inject.Inject;

import org.joda.time.DateTime;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Clob;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.documents.dom.docs.Document;
import org.incode.module.documents.dom.docs.DocumentRepository;
import org.incode.module.documents.dom.docs.DocumentSort;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RendererWithPreviewAsClob;
import org.incode.module.documents.dom.types.DocumentType;

import freemarker.template.TemplateException;

public class RendererForFreemarker implements Renderer, RendererWithPreviewAsClob {

    @Override
    public Document render(
            final DocumentTemplate template,
            final Object dataModel,
            final String documentName) {

        final DocumentType documentType = template.getType();

        try {
            final DocumentSort sort = template.getSort();
            final DateTime createdAt = clockService.nowAsDateTime();

            switch (sort) {

            case CLOB:
                final Clob clob = asClob(template, dataModel, documentName);
                return documentRepository.createClob(documentType, template.getAtPath(), clob, createdAt);

            case TEXT:
                final String textChars = asChars(template, dataModel);

                return documentRepository.createText(documentType, template.getAtPath(), documentName, template.getMimeType(), textChars, createdAt);

            case BLOB:
                throw new IllegalStateException("This renderer cannot render to bytes");

            }
            return null;

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public Clob previewAsClob(
            final DocumentTemplate documentTemplate, final Object dataModel, final String documentName)
            throws IOException {
        return asClob(documentTemplate, dataModel, documentName);
    }

    private Clob asClob(final DocumentTemplate template, final Object dataModel, final String documentName)
            throws IOException {
        final String clobChars = asChars(template, dataModel);
        return new Clob(documentName, template.getMimeType(), clobChars);
    }

    private String asChars(
            final DocumentTemplate documentTemplate,
            final Object dataModel) throws IOException {

        final DocumentType documentType = documentTemplate.getType();
        final String typeRef = documentType.getReference();
        final String atPath = documentTemplate.getAtPath();

        try {
            return freeMarkerService.render(typeRef, atPath, dataModel);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    @Inject
    private DocumentRepository documentRepository;
    @Inject
    private ClockService clockService;


    @Inject
    private FreeMarkerService freeMarkerService;

}
