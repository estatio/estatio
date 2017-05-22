package org.estatio.capex.dom.invoice.payment.approval;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.invoice.payment.PaymentRepository;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class PaymentApprovalInitiatorSubscribingToIncomingInvoiceApprovalState extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(BankAccountVerificationStateTransitionType.TransitionEvent ev) {

        if (ev.getPhase()==StateTransitionEvent.Phase.TRANSITIONED &&
                ev.getTransitionType()==BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT
                ){
            BankAccount bankAccount = ev.getDomainObject();
//            Payment newPayment = paymentRepository.create(invoice.getGrossAmount(), invoice, invoice.getPaymentMethod());
//            transactionService.flushTransaction();
//            stateTransitionService.trigger(newPayment, PaymentApprovalStateTransitionType.INSTANTIATE, null);


        }
    }


    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    TransactionService transactionService;

    @Inject
    PaymentRepository paymentRepository;

}
