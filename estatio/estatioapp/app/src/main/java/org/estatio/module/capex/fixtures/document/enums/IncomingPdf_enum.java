package org.estatio.module.capex.fixtures.document.enums;

import java.util.List;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.fixtures.document.builders.IncomingPdfBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum IncomingPdf_enum implements PersonaWithBuilderScript<Document, IncomingPdfBuilder>, PersonaWithFinder<Document> {

    FakeInvoice1(IncomingPdf_enum.class, "fakeInvoice1.pdf"),
    FakeInvoice2(IncomingPdf_enum.class, "fakeInvoice2.pdf"),
    FakeInvoice3(IncomingPdf_enum.class, "fakeInvoice3.pdf"),
    FakeOrder1(IncomingPdf_enum.class, "fakeOrder1.pdf"),
    FakeOrder2(IncomingPdf_enum.class, "fakeOrder2.pdf"),
    ;

    private final Class<?> contextClass;
    private final String resourceName;

    @Override
    public IncomingPdfBuilder builder() {
        return new IncomingPdfBuilder()
                .setContextClass(contextClass)
                .setResourceName(resourceName);
    }

    @Override
    public Document findUsing(final ServiceRegistry2 serviceRegistry) {

        final IncomingDocumentRepository incomingDocumentRepository = serviceRegistry
                .lookupService(IncomingDocumentRepository.class);
        final List<Document> documents = incomingDocumentRepository.matchAllIncomingDocumentsByName(resourceName);
        return documents.isEmpty() ? null : documents.get(0);
    }
}
