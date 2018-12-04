package org.estatio.module.capex.app.contributions.approval;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.app.SwitchViewService;
import org.estatio.module.capex.app.invoice.IncomingDocAsInvoiceViewModel;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_noAdvise;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_triggerAbstract;
import org.estatio.module.party.dom.Person;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_noAdvise extends IncomingInvoice_noAdvise {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_noAdvise(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    public static class ActionDomainEvent
        extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingDocAsInvoiceViewModel_noAdvise> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-thumbs-o-down")
    public Object act(
            @Nullable final String roleToAssignNextTo,
            @Nullable final Person personToAssignNextTo,
            final String comment, // there should be a reason why
            final boolean goToNext) {
        return super.act(roleToAssignNextTo, personToAssignNextTo, comment, goToNext);
    }


    @Override
    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return switchViewService.switchViewIfPossible(incomingInvoice);
    }

    @Inject
    SwitchViewService switchViewService;

}
