package org.incode.module.document.fixture;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

@Programmatic
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
            final String nameText,
            final ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                                                    .findByTypeAndAtPathAndDate(documentType, atPath, date);
        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.setName(name);
            documentTemplate.setMimeType(mimeType);
            documentTemplate.setText(contentText);
            documentTemplate.setNameText(nameText);
        } else {
            documentTemplate =
                    documentTemplateRepository.createText(
                            documentType, date, atPath,
                            fileSuffix, previewOnly,
                            name, mimeType,
                            contentText,
                            nameText);
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
            final String nameText,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                .findByTypeAndAtPathAndDate(documentType, atPath, date);

        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.modifyClob(clob);
            documentTemplate.setNameText(nameText);
        } else {
            documentTemplate =
                    documentTemplateRepository.createClob(
                            documentType, date, atPath,
                            fileSuffix, previewOnly, clob,
                            nameText);
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
            final String nameText,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                .findByTypeAndAtPathAndDate(documentType, atPath, date);
        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.modifyBlob(blob);
            documentTemplate.setNameText(nameText);
        } else {
            documentTemplate =
                    documentTemplateRepository.createBlob(
                            documentType, date, atPath,
                            fileSuffix, previewOnly, blob,
                            nameText);
        }

        return executionContext.addResult(this, documentTemplate);
    }

    protected static String buildTemplateName(
            final DocumentType docType,
            final String templateNameSuffixIfAny) {
        return buildTemplateName(docType, templateNameSuffixIfAny, null);
    }

    protected static String buildTemplateName(
            final DocumentType docType,
            final String templateNameSuffixIfAny,
            final String extension) {
        final String name = docType.getName() + (templateNameSuffixIfAny != null ? templateNameSuffixIfAny : "");
        return extension != null
                ? name.endsWith(extension)
                ? name
                : name + extension
                : name;
    }


    @Inject
    protected DocumentTemplateRepository documentTemplateRepository;

    @Inject
    protected DocumentTypeRepository documentTypeRepository;

}
