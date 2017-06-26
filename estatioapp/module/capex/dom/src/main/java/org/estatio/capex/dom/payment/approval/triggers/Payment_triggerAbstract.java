package org.estatio.capex.dom.payment.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;

import org.estatio.capex.dom.payment.Payment;
import org.estatio.capex.dom.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

abstract class Payment_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    Payment,
                                    PaymentApprovalStateTransition,
                                    PaymentApprovalStateTransitionType,
                                    PaymentApprovalState> {

    Payment_triggerAbstract(
            final Payment payment,
            final List<PaymentApprovalState> fromStates,
            final PaymentApprovalStateTransitionType requiredTransitionTypeIfAny) {
        super(payment, PaymentApprovalStateTransition.class, fromStates, requiredTransitionTypeIfAny);
    }

    Payment_triggerAbstract(
            final Payment payment,
            final PaymentApprovalStateTransitionType requiredTransitionType) {
        super(payment, PaymentApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType);
    }

    @Action()
    public Payment act(@Nullable final String comment) {
        trigger(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

}
