package org.estatio.module.capex.dom.invoice.approval;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.payment.PaymentLineRepository;
import org.estatio.module.capex.dom.payment.approval.triggers.PaymentBatch_confirmAuthorisation;
import org.estatio.module.task.dom.state.StateTransitionEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoicePaidSubscriber extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoiceApprovalStateTransitionType.TransitionEvent ev) {

        if(ev.getPhase() != StateTransitionEvent.Phase.TRANSITIONED) {
            return;
        }

        final IncomingInvoiceApprovalStateTransitionType transitionType = ev.getTransitionType();
        switch (transitionType) {
        case PAY_BY_IBP:
            List<PaymentLine> paymentLines = paymentLineRepository.findByInvoice(ev.getDomainObject());
            List<PaymentBatch> paymentBatches = paymentLines.stream().map(PaymentLine::getBatch).distinct().collect(Collectors.toList());

            for (PaymentBatch paymentBatch : paymentBatches) {
                for (PaymentLine paymentLine : paymentBatch.getLines()) {
                    if (paymentLine.getInvoice().getApprovalState() != IncomingInvoiceApprovalState.PAID) {
                        return;
                    }
                }
                factoryService.mixin(PaymentBatch_confirmAuthorisation.class, paymentBatch).act("Automatic");

            }

            break;
        }
    }


    @Inject
    FactoryService factoryService;

    @Inject
    PaymentLineRepository paymentLineRepository;

}
