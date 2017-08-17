package org.estatio.capex.dom.payment.contributed;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.capex.dom.payment.PaymentLineRepository;

/**
 * TODO: inline this mixin (unless payments is gonna be decoupled?)
 */
@Mixin
public class IncomingInvoice_payments {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_payments(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<PaymentLine> $$() {
        return paymentLineRepository.findByInvoice(incomingInvoice);
    }

    @Inject PaymentLineRepository paymentLineRepository;

}
