package org.estatio.capex.dom.invoice.manager;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incomingInvoice.IncomingInvoiceManagerMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "200"
)
public class IncomingInvoiceManagerMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public IncomingInvoiceDownloadManager downloadInvoices() {
        final IncomingInvoiceDownloadManager incomingInvoiceDownloadManager = new IncomingInvoiceDownloadManager(LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1), null, null);
        serviceRegistry2.injectServicesInto(incomingInvoiceDownloadManager);
        return incomingInvoiceDownloadManager.init();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
