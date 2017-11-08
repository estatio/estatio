package org.estatio.capex.dom.invoice.manager;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;

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
    public IncomingInvoiceDownloadManager downloadInvoices(
            @Nullable
            final Property property) {

        final IncomingInvoiceDownloadManager incomingInvoiceDownloadManager =
                new IncomingInvoiceDownloadManager(property, null, null);
        serviceRegistry2.injectServicesInto(incomingInvoiceDownloadManager);

        return incomingInvoiceDownloadManager;
    }

    public List<Property> choices0DownloadInvoices() {
        return propertyRepository.allProperties();
    }


    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    PropertyRepository propertyRepository;

}
