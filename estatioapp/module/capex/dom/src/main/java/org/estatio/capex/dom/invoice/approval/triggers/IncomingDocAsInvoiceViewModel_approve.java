package org.estatio.capex.dom.invoice.approval.triggers;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_approve extends IncomingInvoice_approve {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_approve(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    @Override
    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return switchViewService.switchViewIfPossible(incomingInvoice);
    }

    @Inject
    SwitchViewService switchViewService;

}
