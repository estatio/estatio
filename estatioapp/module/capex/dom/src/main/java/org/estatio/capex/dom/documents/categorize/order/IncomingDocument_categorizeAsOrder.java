package org.estatio.capex.dom.documents.categorize.order;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.documents.IncomingDocumentViewModel;

@Mixin(method = "act")
public class IncomingDocument_categorizeAsOrder  {

    final IncomingDocumentViewModel viewModel;

    public IncomingDocument_categorizeAsOrder(final IncomingDocumentViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "folder-open-o")
    public CategorizeIncomingOrderViewModel act() {
        return serviceRegistry2.injectServicesInto(
                new CategorizeIncomingOrderViewModel(viewModel));
    }

    @Inject
    public ServiceRegistry2 serviceRegistry2;


}
