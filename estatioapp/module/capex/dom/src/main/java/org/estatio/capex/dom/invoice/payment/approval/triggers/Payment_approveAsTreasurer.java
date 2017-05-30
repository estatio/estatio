package org.estatio.capex.dom.invoice.payment.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;

@Mixin(method="act")
public class Payment_approveAsTreasurer extends Payment_triggerAbstract {

    public Payment_approveAsTreasurer(Payment payment) {
        super(payment, PaymentApprovalStateTransitionType.APPROVE_AS_TREASURER);
    }

    @Action()
    @MemberOrder(sequence = "4")
    public Payment act(@Nullable final String comment) {
        return super.act(comment);
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }
}
