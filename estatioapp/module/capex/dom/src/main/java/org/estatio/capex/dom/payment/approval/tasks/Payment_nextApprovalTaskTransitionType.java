package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.Payment;
import org.estatio.capex.dom.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_nextTaskTransitionTypeAbstract;

@Mixin(method="prop")
public class Payment_nextApprovalTaskTransitionType
        extends DomainObject_nextTaskTransitionTypeAbstract<
                    Payment,
                    PaymentApprovalStateTransition,
                    PaymentApprovalStateTransitionType,
                    PaymentApprovalState> {

    public Payment_nextApprovalTaskTransitionType(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }

    public boolean hideProp() {
        return super.hideProp();
    }

}
