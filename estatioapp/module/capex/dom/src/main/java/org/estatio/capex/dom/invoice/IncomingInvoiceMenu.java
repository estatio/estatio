package org.estatio.capex.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
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
            final String invoiceNumber,
            final Party buyer,
            final Party seller,
            final LocalDate dueDate,
            final LocalDate invoiceDate,
            final PaymentMethod paymentMethod,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate dateReceived
    ){
        final String atPath = "/FRA";
        return incomingInvoiceRepository.create(invoiceNumber, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, InvoiceStatus.NEW, dateReceived, null);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Payment> allPayments(){
        return paymentRepository.listAll();
    }


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    private PaymentRepository paymentRepository;

}
