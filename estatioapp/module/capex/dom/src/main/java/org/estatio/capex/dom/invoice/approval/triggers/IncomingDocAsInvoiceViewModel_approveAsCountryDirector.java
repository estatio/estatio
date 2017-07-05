package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_approveAsCountryDirector extends IncomingInvoice_approveAsCountryDirector {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_approveAsCountryDirector(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    protected Object objectToReturn() {
        return viewModel;
    }

}
