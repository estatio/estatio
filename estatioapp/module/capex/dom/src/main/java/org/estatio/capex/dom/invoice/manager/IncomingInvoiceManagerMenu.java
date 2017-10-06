package org.estatio.capex.dom.invoice.manager;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoiceType;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incomingInvoice.IncomingInvoiceManagerMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "65.3"
)
public class IncomingInvoiceManagerMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public IncomingInvoiceDownloadManager downloadInvoices() {

        final IncomingInvoiceDownloadManager incomingInvoiceDownloadManager =
                new IncomingInvoiceDownloadManager(null, true, null, IncomingInvoiceType.CAPEX);
        serviceRegistry2.injectServicesInto(incomingInvoiceDownloadManager);

        return incomingInvoiceDownloadManager;
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    ClockService clockService;

}
