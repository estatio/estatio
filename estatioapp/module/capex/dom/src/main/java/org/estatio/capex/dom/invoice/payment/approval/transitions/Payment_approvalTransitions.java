package org.estatio.capex.dom.invoice.payment.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.state.DomainObject_transitionsAbstract;

@Mixin(method = "coll")
public class Payment_approvalTransitions extends
        DomainObject_transitionsAbstract<
                        Payment,
                        PaymentApprovalStateTransition,
                        PaymentApprovalStateTransitionType,
                        PaymentApprovalState> {

    public Payment_approvalTransitions(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<PaymentApprovalStateTransition> coll() {
        return super.coll();
    }
}
