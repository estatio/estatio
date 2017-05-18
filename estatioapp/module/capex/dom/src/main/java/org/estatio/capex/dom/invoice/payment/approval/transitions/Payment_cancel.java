package org.estatio.capex.dom.invoice.payment.approval.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;

@Mixin
public class Payment_cancel extends Payment_abstractTransition {

    public Payment_cancel(Payment payment) {
        super(payment, PaymentApprovalStateTransitionType.CANCEL);
    }

    @Action()
    @MemberOrder(sequence = "9")
    public Payment $$(@Nullable final String comment) {
        return super.$$(comment);
    }


}
