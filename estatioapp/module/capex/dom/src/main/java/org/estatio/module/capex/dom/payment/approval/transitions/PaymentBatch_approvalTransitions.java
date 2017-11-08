package org.estatio.module.capex.dom.payment.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.dobj.DomainObject_transitionsAbstract;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
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
