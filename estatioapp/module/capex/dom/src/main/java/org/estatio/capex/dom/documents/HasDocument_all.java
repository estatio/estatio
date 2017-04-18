package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import lombok.Getter;

@Mixin(method = "act")
public class HasDocument_all {

    public static class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<HasDocument_all> {
    }

    @Getter
    private final HasDocument hasDocument;

    @Inject
    public ServiceRegistry2 serviceRegistry2;

    public HasDocument_all(final HasDocument hasDocument) {
        this.hasDocument = hasDocument;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(cssClassFa = "list")
    public List<IncomingDocumentViewModel> act() {
        return mapper.map(repository.findIncomingDocuments());
    }

    public String disableAct() {
        return repository.findIncomingDocuments().isEmpty() ? "No more documents" : null;
    }

    @Inject
    IncomingDocumentRepository repository;

    @Inject
    IncomingDocumentViewModel.Mapper mapper;

}
