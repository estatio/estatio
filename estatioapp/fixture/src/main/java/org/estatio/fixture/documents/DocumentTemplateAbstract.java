package org.estatio.fixture.documents;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.templates.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;

import org.estatio.fixture.EstatioFixtureScript;

public abstract class DocumentTemplateAbstract extends EstatioFixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected DocumentTemplate createDocumentTemplate(
            final DocumentType documentType,
            final LocalDate date, final String name,
            final String mimeType,
            final String atPath,
            final String text, final String dataModelClassName,
            final RenderingStrategy renderingStrategy,
            ExecutionContext executionContext) {

        final DocumentTemplate documentTemplate = documentTemplateRepository
                .createText(documentType, date, atPath, name, mimeType, text, dataModelClassName, renderingStrategy);
        return executionContext.addResult(this, documentTemplate);
    }

    @Inject
    protected DocumentTemplateRepository documentTemplateRepository;

}
