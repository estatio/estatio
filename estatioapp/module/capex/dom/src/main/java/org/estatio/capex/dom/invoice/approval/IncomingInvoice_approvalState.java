package org.estatio.capex.dom.invoice.approval;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.DomainObject_currentStateAbstract;

@Mixin(method="prop")
public class IncomingInvoice_approvalState
        extends DomainObject_currentStateAbstract<
                            IncomingInvoice,
                            IncomingInvoiceApprovalStateTransition,
                            IncomingInvoiceApprovalStateTransitionType,
                            IncomingInvoiceApprovalState> {

    public IncomingInvoice_approvalState(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public IncomingInvoiceApprovalState prop() {
        return super.prop();
    }
}
