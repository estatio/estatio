package org.estatio.capex.dom.invoice.approval.triggers;

import java.util.List;

import org.apache.isis.applib.annotation.Action;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

public abstract class IncomingInvoice_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    IncomingInvoice,
                                    IncomingInvoiceApprovalStateTransition,
                                    IncomingInvoiceApprovalStateTransitionType,
                                    IncomingInvoiceApprovalState> {

    protected IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final List<IncomingInvoiceApprovalState> fromStates,
            final IncomingInvoiceApprovalStateTransitionType requiredTransitionTypeIfAny) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, fromStates, requiredTransitionTypeIfAny
        );
    }

    protected IncomingInvoice_triggerAbstract(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransitionType requiredTransitionType) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType
        );
    }

    @Action()
    public IncomingInvoice act(final String comment) {
        trigger(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

}
