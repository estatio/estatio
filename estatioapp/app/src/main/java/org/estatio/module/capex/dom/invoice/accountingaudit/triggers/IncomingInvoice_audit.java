package org.estatio.module.capex.dom.invoice.accountingaudit.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;


@Mixin(method = "act")
public class IncomingInvoice_audit extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_audit(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceAccountingStateTransitionType.AUDIT);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_audit> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-check-circle")
    public Object act(
            final IPartyRoleType roleToAssignNextTo,
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

    public IPartyRoleType default0Act() {
        return choices0Act().stream().findFirst().orElse(null);
    }

    public List<? extends IPartyRoleType> choices0Act() {
        return enumPartyRoleType();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public Person default1Act(final IPartyRoleType roleType) {
        return defaultPersonToAssignNextTo(roleType);
    }

    public List<Person> choices1Act(final IPartyRoleType roleType) {
        return choicesPersonToAssignNextTo(roleType);
    }

}
