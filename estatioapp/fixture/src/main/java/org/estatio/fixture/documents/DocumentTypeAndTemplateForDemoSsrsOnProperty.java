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
package org.estatio.fixture.documents;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.types.DocumentType;

import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;

public class DocumentTypeAndTemplateForDemoSsrsOnProperty extends DocumentTemplateAbstract {

    public static final String AT_PATH = ApplicationTenancyForGlobal.PATH;

    public static final String DEMO_SSRS_GLOBAL = "DEMO-SSRS/";

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new RenderingStrategyForSsrs());
        }

        final RenderingStrategy renderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyForSsrs.REF);

        createTypeAndTemplate(
                DEMO_SSRS_GLOBAL,
                "Demo for SRSS Rendering Strategy (to PDF)",
                AT_PATH,
                renderingStrategy,
                "http://www.pdfpdf.com/samples/Sample5.PDF",
                executionContext);

    }

    protected DocumentTemplate createTypeAndTemplate(
            final String docTypeRef,
            final String docTypeName,
            final String atPath,
            final RenderingStrategy renderingStrategy,
            final String templateText,
            final ExecutionContext executionContext) {

        final DocumentType docType = documentTypeRepository.create(docTypeRef, docTypeName);

        final LocalDate now = clockService.now();

        final ApplicationTenancy appTenancy = applicationTenancyRepository.findByPath(atPath);

        return createDocumentTextTemplate(
                docType, now, docType.getName(),
                "application/pdf",
                appTenancy.getPath(),
                templateText,
                StringInterpolatorService.Root.class.getName(),
                renderingStrategy,
                executionContext);
    }

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    private ClockService clockService;


}
