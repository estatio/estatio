package org.estatio.capex.dom.documents.categorize.invoice;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.documents.IncomingDocumentViewModel;

@Mixin(method = "act")
public class IncomingDocument_categorizeAsInvoice  {

    protected final IncomingDocumentViewModel viewModel;

    public IncomingDocument_categorizeAsInvoice(final IncomingDocumentViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "folder-open-o")
    public CategorizeIncomingInvoiceViewModel act() {
        return serviceRegistry2.injectServicesInto(
                new CategorizeIncomingInvoiceViewModel(viewModel));
    }

    @Inject
    public ServiceRegistry2 serviceRegistry2;


}
