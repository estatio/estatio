package org.estatio.capex.dom.invoice.payment.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;

@Mixin(method="act")
public class Payment_approveAsFinancialDirector extends Payment_triggerAbstract {

    private final Payment payment;

    public Payment_approveAsFinancialDirector(Payment payment) {
        super(payment, PaymentApprovalStateTransitionType.APPROVE_AS_FINANCIAL_DIRECTOR);
        this.payment = payment;
    }

}
