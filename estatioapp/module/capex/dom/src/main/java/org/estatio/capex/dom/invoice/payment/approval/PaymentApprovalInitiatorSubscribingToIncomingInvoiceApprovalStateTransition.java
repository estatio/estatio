package org.estatio.capex.dom.invoice.payment.approval;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.PaymentRepository;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class PaymentApprovalInitiatorSubscribingToIncomingInvoiceApprovalStateTransition extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(IncomingInvoiceApprovalStateTransitionType.TransitionEvent ev) {

        if (ev.getPhase()==StateTransitionEvent.Phase.TRANSITIONED &&
                ev.getTransitionType()==IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR
                ){
            IncomingInvoice invoice = ev.getDomainObject();
            Payment newPayment = paymentRepository.create(invoice.getGrossAmount(), invoice, invoice.getPaymentMethod());
            transactionService.flushTransaction();
            stateTransitionService.apply(newPayment, PaymentApprovalStateTransitionType.INSTANTIATE, null);
        }
    }


    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    TransactionService transactionService;

    @Inject
    PaymentRepository paymentRepository;

}
