package org.estatio.capex.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.capex.dom.invoice.IncomingInvoiceMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Incoming invoices"
)
public class IncomingInvoiceMenu {

    public List<IncomingInvoice> allInvoices(){
        return incomingInvoiceRepository.listAll();

    }

    public IncomingInvoice newInvoice(
            final String invoiceNumber,
            final Party buyer,
            final Party seller,
            final LocalDate dueDate,
            final LocalDate invoiceDate,
            final PaymentMethod paymentMethod
    ){
        final String atPath = "/FRA";
        return incomingInvoiceRepository.create(invoiceNumber, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, InvoiceStatus.NEW);
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

}
