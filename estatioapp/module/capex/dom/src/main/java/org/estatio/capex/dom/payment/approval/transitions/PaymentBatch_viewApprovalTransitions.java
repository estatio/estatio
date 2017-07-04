package org.estatio.capex.dom.payment.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_viewTransitionsAbstract;

@Mixin(method = "act")
public class PaymentBatch_viewApprovalTransitions extends
        DomainObject_viewTransitionsAbstract<
                PaymentBatch,
                PaymentBatchApprovalStateTransition,
                PaymentBatchApprovalStateTransitionType,
                PaymentBatchApprovalState> {

    public PaymentBatch_viewApprovalTransitions(final PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<PaymentBatchApprovalStateTransition> act() {
        return super.act();
    }
}
