package org.estatio.fixture.documents;

import javax.inject.Inject;

import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.fixture.EstatioFixtureScript;

public abstract class DocumentTypeAbstract extends EstatioFixtureScript {

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
