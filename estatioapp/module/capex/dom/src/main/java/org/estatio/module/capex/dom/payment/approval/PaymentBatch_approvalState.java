package org.estatio.module.capex.dom.payment.approval;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.dobj.DomainObject_currentStateAbstract;
import org.estatio.module.capex.dom.payment.PaymentBatch;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
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
