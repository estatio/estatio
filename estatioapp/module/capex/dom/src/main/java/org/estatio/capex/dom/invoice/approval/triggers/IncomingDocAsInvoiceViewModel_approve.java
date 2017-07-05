package org.estatio.capex.dom.invoice.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_approve extends IncomingInvoice_approve {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_approve(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    protected Object objectToReturn() {
        return viewModel;
    }

}
