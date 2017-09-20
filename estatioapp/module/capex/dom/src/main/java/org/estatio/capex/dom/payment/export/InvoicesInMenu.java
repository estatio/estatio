package org.estatio.capex.dom.payment.export;

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
        objectType = "payments.export.InvoicesInMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Payments",
        menuOrder = "70.8"
)
public class InvoicesInMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public PaymentLineDownloadManager downloadPayments() {
        final PaymentLineDownloadManager incomingInvoiceDownloadManager = new PaymentLineDownloadManager(LocalDate.now().withDayOfMonth(1));
        serviceRegistry2.injectServicesInto(incomingInvoiceDownloadManager);
        return incomingInvoiceDownloadManager.init();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
