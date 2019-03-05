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
public class IncomingInvoice_complete extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_complete(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_complete> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public IncomingInvoice act(
            final String role,
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment) {
        trigger(personToAssignNextTo, comment, comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public String default0Act() {
        return enumPartyRoleTypeName();
    }

    public Person default1Act() {
        return defaultPersonToAssignNextTo();
    }

    public List<Person> choices1Act() {
        return choicesPersonToAssignNextTo();
    }


}
