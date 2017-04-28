package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;

// TODO: remove class
// @Mixin(method = "act")
public class HasDocument_previous {

    public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<HasDocument_all> {
    }

    private final HasDocument hasDocument;

    public HasDocument_previous(final HasDocument hasDocument) {
        this.hasDocument = hasDocument;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(cssClassFa = "step-backward")
    public HasDocumentAbstract act() {
        return factory.map(previousDocument());
    }

    private Document previousDocument() {
        Document currentDocument = currentDocument();
        List<Document> incomingDocuments = repository.findWithNoPaperclips();
        for (int i = 0; i < incomingDocuments.size(); i++) {
            final int previous = i-1;
            final Document document = incomingDocuments.get(i);
            if(document == currentDocument && previous >= 0) {
                return incomingDocuments.get(previous);
            }
        }
        return currentDocument;
    }

    private Document currentDocument() {
        return hasDocument.getDocument();
    }

    public String disableAct() {
        return previousDocument() ==  currentDocument()
                ? "No more documents"
                : null;
    }

    @Inject
    public ServiceRegistry2 serviceRegistry2;

    @Inject
    DocumentRepository repository;

    @Inject
    HasDocumentAbstract.Factory factory;
}
