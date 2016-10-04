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
package org.estatio.fixture.documents.demo;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;
import org.incode.module.documents.fixture.DocumentTemplateFSAbstract;

import org.estatio.dom.WithNameGetter;
import org.estatio.dom.documents.binders.BinderForWithNameGetterAttachToInput;
import org.estatio.fixture.documents.RenderingStrategies;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;

public class DocumentTypeAndTemplateFSForHelloGlobal extends DocumentTemplateFSAbstract {

    public static final String TYPE_REF = "HELLO";
    public static final String AT_PATH = ApplicationTenancyForGlobal.PATH;

    public static final String TEMPLATE_NAME = "Hello template";
    public static final String TEMPLATE_MIME_TYPE = "text/plain";
    public static final String FILE_SUFFIX = ".txt";

    public static final String CONTENT_TEXT = "Hello ${user}";
    public static final String SUBJECT_TEXT = "Hello ${user}";

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ApplicationTenancyForGlobal());
        executionContext.executeChild(this, new RenderingStrategies());

        upsertType(TYPE_REF, "Hello world!", executionContext);

        final DocumentType documentType = documentTypeRepository.findByReference(TYPE_REF);
        final RenderingStrategy freemarkerRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_FMK);
        final LocalDate date = clockService.now();

        final DocumentTemplate documentTemplate = upsertDocumentTextTemplate(
                documentType, date, AT_PATH, FILE_SUFFIX, false, TEMPLATE_NAME, TEMPLATE_MIME_TYPE,
                SUBJECT_TEXT, freemarkerRenderingStrategy, CONTENT_TEXT, freemarkerRenderingStrategy,
                executionContext);

        documentTemplate.applicable(WithNameGetter.class, BinderForWithNameGetterAttachToInput.class);

        executionContext.addResult(this, documentTemplate);
    }


    @Inject
    ClockService clockService;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    RenderingStrategyRepository renderingStrategyRepository;


}
