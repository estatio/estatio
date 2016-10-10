/*
 *  Copyright 2016 Eurocommercial Properties NV
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
package org.incode.module.documents.fixture;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;

public abstract class DocumentTemplateFSAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    /**
     * convenience, as templates and types often created together
     * @param reference
     * @param name
     * @param executionContext
     * @return
     */
    protected DocumentType upsertType(
            String reference,
            String name,
            ExecutionContext executionContext) {

        DocumentType documentType = documentTypeRepository.findByReference(reference);
        if(documentType != null) {
            documentType.setName(name);
        } else {
            documentType = documentTypeRepository.create(reference, name);
        }
        return executionContext.addResult(this, documentType);
    }


    protected DocumentTemplate upsertDocumentTextTemplate(
            final DocumentType documentType,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String name,
            final String mimeType,
            final String contentText,
            final RenderingStrategy contentRenderingStrategy,
            final String nameText,
            final RenderingStrategy nameRenderingStrategy,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                                                    .findByTypeAndAtPathAndDate(documentType, atPath, date);
        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.setName(name);
            documentTemplate.setMimeType(mimeType);
            documentTemplate.setText(contentText);
            documentTemplate.setContentRenderingStrategy(contentRenderingStrategy);
            documentTemplate.setNameText(nameText);
            documentTemplate.setNameRenderingStrategy(nameRenderingStrategy);
        } else {
            documentTemplate =
                    documentTemplateRepository.createText(
                            documentType, date, atPath,
                            fileSuffix, previewOnly,
                            name, mimeType,
                            contentText, contentRenderingStrategy,
                            nameText, nameRenderingStrategy);
        }
        return executionContext.addResult(this, documentTemplate);
    }

    protected DocumentTemplate upsertDocumentClobTemplate(
            final DocumentType documentType,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Clob clob,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy nameRenderingStrategy,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                .findByTypeAndAtPathAndDate(documentType, atPath, date);

        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.setClob(clob);
            documentTemplate.setContentRenderingStrategy(contentRenderingStrategy);
            documentTemplate.setNameText(subjectText);
            documentTemplate.setNameRenderingStrategy(nameRenderingStrategy);
        } else {
            documentTemplate =
                    documentTemplateRepository.createClob(
                            documentType, date, atPath,
                            fileSuffix, previewOnly, clob,
                            contentRenderingStrategy,
                            subjectText, nameRenderingStrategy);
        }
        return executionContext.addResult(this, documentTemplate);
    }

    protected DocumentTemplate upsertDocumentBlobTemplate(
            final DocumentType documentType,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Blob blob,
            final RenderingStrategy contentRenderingStrategy,
            final String nameText,
            final RenderingStrategy nameRenderingStrategy,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                .findByTypeAndAtPathAndDate(documentType, atPath, date);
        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.setBlob(blob);
            documentTemplate.setContentRenderingStrategy(contentRenderingStrategy);
            documentTemplate.setNameText(nameText);
            documentTemplate.setNameRenderingStrategy(nameRenderingStrategy);
        } else {
            documentTemplate =
                    documentTemplateRepository.createBlob(
                            documentType, date, atPath,
                            fileSuffix, previewOnly, blob,
                            contentRenderingStrategy,
                            nameText, nameRenderingStrategy);
        }

        return executionContext.addResult(this, documentTemplate);
    }

    @Inject
    protected DocumentTemplateRepository documentTemplateRepository;

    @Inject
    protected DocumentTypeRepository documentTypeRepository;

}
