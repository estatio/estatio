package org.estatio.module.capex.app;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.app.paymentline.PaymentLineDownloadManager;

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
    public PaymentLineDownloadManager downloadPaymentsFra() {
        final PaymentLineDownloadManager downloadManager =
                new PaymentLineDownloadManager(clockService.now().withDayOfMonth(1));
        serviceRegistry2.injectServicesInto(downloadManager);
        return downloadManager.init();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    ClockService clockService;

}
