package org.estatio.capex.dom.invoice.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.dobj.DomainObject_viewTransitionsAbstract;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

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
