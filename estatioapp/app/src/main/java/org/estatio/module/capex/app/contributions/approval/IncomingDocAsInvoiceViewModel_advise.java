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
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_advise;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_triggerAbstract;
import org.estatio.module.party.dom.Person;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingDocAsInvoiceViewModel_advise extends IncomingInvoice_advise {

    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewModel_advise(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel.getDomainObject());
        this.viewModel = viewModel;
    }

    public static class ActionDomainEvent
        extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingDocAsInvoiceViewModel_advise> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-check-circle")
    public Object act(
            @Nullable final String roleToAssignNextTo,
            final Person personToAssignNextTo, // should always be directed towards a person
            @Nullable final String comment,
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
