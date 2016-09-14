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
package org.estatio.fixture.documents;

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

public abstract class DocumentTemplateAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    /**
     * convenience, as templates and types often created together
     * @param reference
     * @param name
     * @param executionContext
     * @return
     */
    protected DocumentType createType(
            String reference,
            String name,
            ExecutionContext executionContext) {

        final DocumentType documentType = documentTypeRepository.create(reference, name);
        return executionContext.addResult(this, documentType);
    }


    protected DocumentTemplate createDocumentTextTemplate(
            final DocumentType documentType,
            final LocalDate date,
            final String name,
            final String mimeType,
            final String fileSuffix,
            final String atPath,
            final String text,
            final RenderingStrategy renderingStrategy,
            ExecutionContext executionContext) {

        final DocumentTemplate documentTemplate = documentTemplateRepository
                .createText(documentType, date, atPath, name, mimeType, fileSuffix, text, renderingStrategy);
        return executionContext.addResult(this, documentTemplate);
    }

    protected DocumentTemplate createDocumentClobTemplate(
            final DocumentType documentType,
            final LocalDate date,
            final String atPath,
            final Clob clob,
            final String fileSuffix,
            final RenderingStrategy renderingStrategy,
            ExecutionContext executionContext) {

        final DocumentTemplate documentTemplate = documentTemplateRepository
                .createClob(documentType, date, atPath, clob, fileSuffix, renderingStrategy);
        return executionContext.addResult(this, documentTemplate);
    }

    protected DocumentTemplate createDocumentBlobTemplate(
            final DocumentType documentType,
            final LocalDate date,
            final String atPath,
            final Blob blob,
            final String fileSuffix,
            final RenderingStrategy renderingStrategy,
            ExecutionContext executionContext) {

        final DocumentTemplate documentTemplate = documentTemplateRepository
                .createBlob(documentType, date, atPath, blob, fileSuffix, renderingStrategy);
        return executionContext.addResult(this, documentTemplate);
    }

    @Inject
    protected DocumentTemplateRepository documentTemplateRepository;

    @Inject
    protected DocumentTypeRepository documentTypeRepository;

}
