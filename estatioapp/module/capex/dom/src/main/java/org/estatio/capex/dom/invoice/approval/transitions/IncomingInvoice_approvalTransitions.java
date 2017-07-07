package org.estatio.capex.dom.invoice.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.dobj.DomainObject_transitionsAbstract;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;

@Mixin(method = "coll")
public class IncomingInvoice_approvalTransitions
        extends DomainObject_transitionsAbstract<
                                        IncomingInvoice,
                                        IncomingInvoiceApprovalStateTransition,
                                        IncomingInvoiceApprovalStateTransitionType,
                                        IncomingInvoiceApprovalState> {

    public IncomingInvoice_approvalTransitions(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice,
                IncomingInvoiceApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<IncomingInvoiceApprovalStateTransition> coll() {
        return super.coll();
    }

}
