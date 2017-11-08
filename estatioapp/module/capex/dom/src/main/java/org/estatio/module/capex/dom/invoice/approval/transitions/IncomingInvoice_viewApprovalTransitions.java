package org.estatio.module.capex.dom.invoice.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.dobj.DomainObject_viewTransitionsAbstract;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingInvoice_viewApprovalTransitions
        extends DomainObject_viewTransitionsAbstract<
                                IncomingInvoice,
                                IncomingInvoiceApprovalStateTransition,
                                IncomingInvoiceApprovalStateTransitionType,
                                IncomingInvoiceApprovalState> {

    public IncomingInvoice_viewApprovalTransitions(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice,
                IncomingInvoiceApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<IncomingInvoiceApprovalStateTransition> act() {
        return super.act();
    }

}
