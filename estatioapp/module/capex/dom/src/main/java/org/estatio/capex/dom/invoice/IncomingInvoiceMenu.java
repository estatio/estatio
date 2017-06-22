package org.estatio.capex.dom.invoice;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.PaymentRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incomingInvoice.IncomingInvoiceMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In"
)
public class IncomingInvoiceMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> allInvoices(){
        return incomingInvoiceRepository.listAll();
    }

    public IncomingInvoice newInvoice(
            final IncomingInvoice.Type type,
            final String invoiceNumber,
            final Party buyer,
            final Party seller,
            final LocalDate dueDate,
            final LocalDate invoiceDate,
            final PaymentMethod paymentMethod,
            @Nullable final LocalDate dateReceived
    ) {
        return incomingInvoiceRepository.create(type, invoiceNumber, buyer.getAtPath(), buyer, seller, invoiceDate, dueDate, paymentMethod, InvoiceStatus.NEW, dateReceived, null);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Payment> allPayments(){
        return paymentRepository.listAll();
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    private PaymentRepository paymentRepository;

}
