package org.estatio.capex.dom.payment.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.dobj.DomainObject_transitionsAbstract;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

@Mixin(method = "coll")
public class PaymentBatch_approvalTransitions extends
        DomainObject_transitionsAbstract<
                        PaymentBatch,
                        PaymentBatchApprovalStateTransition,
                        PaymentBatchApprovalStateTransitionType,
                        PaymentBatchApprovalState> {

    public PaymentBatch_approvalTransitions(final PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<PaymentBatchApprovalStateTransition> coll() {
        return super.coll();
    }
}
