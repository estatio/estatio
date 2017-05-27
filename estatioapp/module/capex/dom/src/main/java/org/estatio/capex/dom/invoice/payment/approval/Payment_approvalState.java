package org.estatio.capex.dom.invoice.payment.approval;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.state.DomainObject_currentStateAbstract;

@Mixin(method="prop")
public class Payment_approvalState
        extends DomainObject_currentStateAbstract<
                        Payment,
                        PaymentApprovalStateTransition,
                        PaymentApprovalStateTransitionType,
                PaymentApprovalState> {

    public Payment_approvalState(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public PaymentApprovalState prop() {
        return super.prop();
    }
}
