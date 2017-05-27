package org.estatio.capex.dom.documents;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_categoriseAsInvoice {

    private final Document document;

    public Document_categoriseAsInvoice(final Document document) {
        this.document = document;
    }

    public static class DomainEvent extends ActionDomainEvent<Document> {}

    @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceViewModel act() {
        final IncomingInvoiceViewModel viewModel = new IncomingInvoiceViewModel(this.document);
        return serviceRegistry2.injectServicesInto(viewModel).init();
    }

    public boolean hideAct() {
        return !DocumentTypeData.INCOMING_INVOICE.isDocTypeFor(document);
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
