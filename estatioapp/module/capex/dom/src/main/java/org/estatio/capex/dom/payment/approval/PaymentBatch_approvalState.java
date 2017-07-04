package org.estatio.capex.dom.payment.approval;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.dobj.DomainObject_currentStateAbstract;

@Mixin(method="prop")
public class PaymentBatch_approvalState
        extends DomainObject_currentStateAbstract<
        PaymentBatch,
        PaymentBatchApprovalStateTransition,
        PaymentBatchApprovalStateTransitionType,
        PaymentBatchApprovalState> {

    public PaymentBatch_approvalState(final PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public PaymentBatchApprovalState prop() {
        return super.prop();
    }
}
