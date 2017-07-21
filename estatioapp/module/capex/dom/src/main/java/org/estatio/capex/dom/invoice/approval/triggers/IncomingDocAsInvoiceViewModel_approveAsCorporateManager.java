package org.estatio.capex.dom.invoice.approval.triggers;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_approveAsCorporateManager
                    extends IncomingInvoice_approveAsCorporateManager {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_approveAsCorporateManager(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return switchViewService.switchViewIfPossible(incomingInvoice);
    }

    @Inject
    SwitchViewService switchViewService;

}
