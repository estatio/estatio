package org.estatio.capex.dom.paydd;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.paydd.manager.DirectDebitsManager;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "payments.PayDirectDebitMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Payments",
        menuOrder = "70.4"
)
public class DirectDebitsMenu {



    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-check-square-o")
    @MemberOrder(sequence = "300.10")
    public DirectDebitsManager openDirectDebitManager() {
        final DirectDebitsManager directDebitsManager = new DirectDebitsManager();
        serviceRegistry2.injectServicesInto(directDebitsManager);
        return directDebitsManager;
    }


    @Inject
    ServiceRegistry2 serviceRegistry2;

}
