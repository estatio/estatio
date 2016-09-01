package org.estatio.fixture.documents;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.templates.DocumentTemplate;
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

        final DocumentTemplate documentTemplate = createDocumentTemplate(
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
