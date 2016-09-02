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

import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

import org.estatio.app.menus.demo.HelloDocumentTemplateUserDataModel;
import org.estatio.fixture.EstatioBaseLineFixture;

public class DocumentTemplateForHelloGlobal extends DocumentTemplateAbstract {

    public static final String TYPE_REF = DocumentTypeForHello.REF;
    public static final String AT_PATH = "/";

    public static final String RENDERING_STRATEGY_REF = RenderingStrategyForFreemarker.REF;
    public static final String TEMPLATE_NAME = "Hello template";
    public static final String TEMPLATE_MIME_TYPE = "text/plain";

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new EstatioBaseLineFixture());
            executionContext.executeChild(this, new DocumentTypeForHello());
            executionContext.executeChild(this, new RenderingStrategyForFreemarker());
        }

        final DocumentType documentType = documentTypeRepository.findByReference(TYPE_REF);
        final RenderingStrategy renderingStrategy = renderingStrategyRepository.findByReference(RENDERING_STRATEGY_REF);
        final LocalDate date = clockService.now();

        final DocumentTemplate documentTemplate = createDocumentTextTemplate(
                documentType, date, TEMPLATE_NAME, TEMPLATE_MIME_TYPE, AT_PATH,
                "Hello ${user}",
                HelloDocumentTemplateUserDataModel.class.getName(), renderingStrategy, executionContext);

        executionContext.addResult(this, documentTemplate);
    }


    @Inject
    ClockService clockService;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    RenderingStrategyRepository renderingStrategyRepository;


}
