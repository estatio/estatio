package org.estatio.module.capex.dom.invoice.approval.triggers;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingInvoice_approveAsCenterManager extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approveAsCenterManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_approveAsCenterManager> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-thumbs-o-up")
    public Object act(
            @Nullable final IPartyRoleType roleToAssignNextTo,
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment,
            final boolean goToNext) {
        final IncomingInvoice next = nextAfterPendingIfRequested(goToNext);
        trigger(roleToAssignNextTo, personToAssignNextTo, comment, comment);
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

    public IPartyRoleType default0Act(){
        final List<IPartyRoleType> nextRoles = stateTransitionService.peekTaskRoleAssignToAfter(incomingInvoice,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER);
        return nextRoles.isEmpty() ? null : nextRoles.get(0);
    }

    public IPartyRoleType choices0Act(){
        final List<IPartyRoleType> nextRoles = stateTransitionService.peekTaskRoleAssignToAfter(incomingInvoice,
                IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER);
        return nextRoles.isEmpty() ? null : nextRoles.get(0);
    }

    public Person default1Act(final IPartyRoleType roleType) {
        if (roleType==null) return null;
        return defaultPersonToAssignNextTo(roleType);
    }

    public List<Person> choices1Act(final IPartyRoleType roleType) {
        if (roleType==null) return Collections.EMPTY_LIST;
        return choicesPersonToAssignNextTo(roleType);
    }

}
