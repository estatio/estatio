package org.estatio.capex.dom.invoice.approval.triggers;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_approveAsCountryDirector
                   extends IncomingInvoice_approveAsCountryDirector {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_approveAsCountryDirector(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return switchViewService.switchViewIfPossible(incomingInvoice);
    }

    @Inject
    SwitchViewService switchViewService;

}
