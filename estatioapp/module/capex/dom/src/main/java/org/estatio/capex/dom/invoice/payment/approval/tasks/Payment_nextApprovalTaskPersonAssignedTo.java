package org.estatio.capex.dom.invoice.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_nextTaskPersonAssignedToAbstract;

@Mixin(method="prop")
public class Payment_nextApprovalTaskPersonAssignedTo
        extends DomainObject_nextTaskPersonAssignedToAbstract<
                    Payment,
                    PaymentApprovalStateTransition,
                    PaymentApprovalStateTransitionType,
                    PaymentApprovalState> {

    public Payment_nextApprovalTaskPersonAssignedTo(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }

    public boolean hideProp() {
        return super.hideProp();
    }

}
