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

import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolator;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolatorCaptureUrl;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.fixture.DocumentTemplateFSAbstract;
import org.incode.modules.docrendering.freemarker.fixture.RenderingStrategyFSForFreemarker;

import org.estatio.dom.asset.Property;
import org.estatio.dom.documents.binders.BinderForReportServer;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;

public class DocumentTypeAndTemplateFSForDemoSsrsOnProperty extends DocumentTemplateFSAbstract {

    public static final String AT_PATH = ApplicationTenancyForGlobal.PATH;

    public static final String DEMO_SSRS_GLOBAL = "DEMO-SSRS/";
    public static final String DEMO_SSRS_NO_PREVIEW_GLOBAL = "DEMO-SSRS-NO-PREVIEW/";

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl());
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolatorCaptureUrl());
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolator());

        final RenderingStrategy ssrsRenderingStrategy =
                renderingStrategyRepository.findByReference(
                        RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl.REF);
        final RenderingStrategy ssrsNoPreviewRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyFSForStringInterpolatorCaptureUrl.REF);
        final RenderingStrategy freemarkerRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyFSForFreemarker.REF);

        final DocumentTemplate demoTemplate = createTypeAndTemplate(
                DEMO_SSRS_GLOBAL,
                "Demo for SRSS Rendering",
                AT_PATH,
                false,
                "http://www.pdfpdf.com/samples/Sample5.PDF", ssrsRenderingStrategy,
                "Demo for SRSS Rendering-${title}", freemarkerRenderingStrategy,
                executionContext);

        demoTemplate.applicable(Property.class.getName(), BinderForReportServer.class.getName());



        final DocumentTemplate demoNoPreviewTemplate = createTypeAndTemplate(
                DEMO_SSRS_NO_PREVIEW_GLOBAL,
                "Demo for SRSS Rendering, no preview",
                AT_PATH,
                false,
                "http://www.pdfpdf.com/samples/Sample5.PDF", ssrsNoPreviewRenderingStrategy,
                "Demo for SRSS Rendering-${title}", freemarkerRenderingStrategy,
                executionContext);

        demoNoPreviewTemplate.applicable(Property.class.getName(), BinderForReportServer.class.getName());

    }

    protected DocumentTemplate createTypeAndTemplate(
            final String docTypeRef,
            final String docTypeName,
            final String atPath,
            final boolean previewOnly,
            final String templateText, final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy,
            final ExecutionContext executionContext) {

        final DocumentType docType = documentTypeRepository.create(docTypeRef, docTypeName);
        final LocalDate now = clockService.now();
        final ApplicationTenancy appTenancy = applicationTenancyRepository.findByPath(atPath);

        return createDocumentTextTemplate(
                docType, now, appTenancy.getPath(), ".pdf", previewOnly, docType.getName(),
                "application/pdf",
                templateText,
                contentRenderingStrategy,
                subjectText,
                subjectRenderingStrategy,
                executionContext);
    }

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    private ClockService clockService;


}
