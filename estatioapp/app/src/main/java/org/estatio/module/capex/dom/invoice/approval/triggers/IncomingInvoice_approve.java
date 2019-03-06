package org.estatio.module.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.assertj.core.util.Lists;

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
public class IncomingInvoice_approve extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approve(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_approve> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-thumbs-o-up")
    public Object act(
            @Nullable final String roleToAssignNextTo,      // ECP-855: this field serves as a hint to the user to which role the next task will be assigned
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment,
            final boolean goToNext) {
        final IncomingInvoice next = nextAfterPendingIfRequested(goToNext);
        trigger(personToAssignNextTo, comment, comment);
        return objectToReturn(next);
    }

    public boolean default3Act() {
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

    public String default0Act() {
        return enumPartyRoleTypeName().isEmpty() ? "no further tasks" : enumPartyRoleTypeName(); // ECP-855: since this can be the only approval
    }

    public Person default1Act() {
        return defaultPersonToAssignNextTo();
    }

    public List<Person> choices1Act() {
        return enumPartyRoleTypeName().isEmpty() ? Lists.emptyList() : choicesPersonToAssignNextTo(); // ECP-855: since this can be the only approval
    }

}
