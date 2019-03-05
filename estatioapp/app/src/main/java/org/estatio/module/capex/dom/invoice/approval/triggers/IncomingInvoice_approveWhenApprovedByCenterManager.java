package org.estatio.module.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.party.dom.Person;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingInvoice_approveWhenApprovedByCenterManager extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approveWhenApprovedByCenterManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_approveWhenApprovedByCenterManager> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-thumbs-o-up")
    public Object act(
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment,
            final boolean goToNext) {
        final IncomingInvoice next = nextAfterPendingIfRequested(goToNext);
        trigger(personToAssignNextTo, comment, comment);
        return objectToReturn(next);
    }

    public boolean default2Act() {
        return true;
    }

    protected Object objectToReturn(final IncomingInvoice incomingInvoice) {
        return incomingInvoice;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public Person default0Act() {
        return defaultPersonToAssignNextTo();
    }

    public List<Person> choices0Act() {
        return choicesPersonToAssignNextTo();
    }

}
