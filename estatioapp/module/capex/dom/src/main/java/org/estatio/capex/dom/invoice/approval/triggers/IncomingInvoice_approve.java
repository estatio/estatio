package org.estatio.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.dom.party.Person;

@Mixin(method = "act")
public class IncomingInvoice_approve extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_approve(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.APPROVE);
        this.incomingInvoice = incomingInvoice;
    }

    @Action()
    @ActionLayout(cssClassFa = "fa-thumbs-o-up")
    public Object act(
            final String role,
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment) {
        trigger(personToAssignNextTo, comment);
        return objectToReturn();
    }

    protected Object objectToReturn() {
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
