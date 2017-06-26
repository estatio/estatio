package org.estatio.capex.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incomingInvoice.IncomingInvoiceMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "200"
)
public class IncomingInvoiceMenu {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<IncomingInvoice> allInvoices(){
        return incomingInvoiceRepository.listAll();
    }


    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesByInvoiceDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return incomingInvoiceRepository.findByInvoiceDateBetween(fromDate, toDate);
    }

    public LocalDate default0FindInvoicesByInvoiceDateBetween() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindInvoicesByInvoiceDateBetween() {
        return clockService.now();
    }

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesByDueDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return incomingInvoiceRepository.findByDueDateBetween(fromDate, toDate);
    }

    public LocalDate default0FindInvoicesByDueDateBetween() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindInvoicesByDueDateBetween() {
        return clockService.now();
    }


    ///////////////////////////////////////////


    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesByDateReceivedBetween(final LocalDate fromDate, final LocalDate toDate) {
        return incomingInvoiceRepository.findByDateReceivedBetween(fromDate, toDate);
    }

    public LocalDate default0FindInvoicesByDateReceivedBetween() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindInvoicesByDateReceivedBetween() {
        return clockService.now();
    }

    ///////////////////////////////////////////


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    ClockService clockService;

}
