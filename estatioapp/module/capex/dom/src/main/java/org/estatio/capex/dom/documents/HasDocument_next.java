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
public class HasDocument_next {

    public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<HasDocument_all> {
    }

    private final HasDocument hasDocument;

    public HasDocument_next(final HasDocument hasDocument) {
        this.hasDocument = hasDocument;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(cssClassFa = "step-forward")
    public HasDocumentAbstract act() {
        return factory.map(nextDocument());
    }

    private Document nextDocument() {
        Document currentDocument = currentDocument();
        List<Document> incomingDocuments = repository.findWithNoPaperclips();
        for (int i = 0; i < incomingDocuments.size(); i++) {
            final int next = i+1;
            final Document document = incomingDocuments.get(i);
            if(document == currentDocument && next < incomingDocuments.size()) {
                return incomingDocuments.get(next);
            }
        }
        return currentDocument;
    }

    private Document currentDocument() {
        return hasDocument.getDocument();
    }

    public String disableAct() {
        return nextDocument() == currentDocument()
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
