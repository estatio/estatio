package org.incode.module.document.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

public abstract class DocumentTypeFSAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected DocumentType createType(
            String reference,
            String name,
            ExecutionContext executionContext) {

        final DocumentType documentType = documentTypeRepository.create(reference, name);
        return executionContext.addResult(this, documentType);
    }

    @Inject
    protected DocumentTypeRepository documentTypeRepository;


}
