package org.estatio.capex.dom.invoice.payment.approval.triggers;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

abstract class Payment_triggerAbstract
        extends DomainObject_triggerAbstract<
                    Payment,
                    PaymentApprovalStateTransition,
                    PaymentApprovalStateTransitionType,
                    PaymentApprovalState> {

    Payment_triggerAbstract(
            final Payment payment,
            final PaymentApprovalStateTransitionType transitionType) {
        super(payment, transitionType);
    }

}
