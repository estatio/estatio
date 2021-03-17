package org.estatio.module.capex.app;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.app.paydd.DirectDebitsFraManager;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "payments.PayDirectDebitMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Payments",
        menuOrder = "70.2"
)
public class DirectDebitsMenu {



    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-check-square-o")
    @MemberOrder(sequence = "300.10")
    public DirectDebitsFraManager directDebitFraManager() {
        final DirectDebitsFraManager directDebitsFraManager = new DirectDebitsFraManager();
        serviceRegistry2.injectServicesInto(directDebitsFraManager);
        return directDebitsFraManager;
    }


    @Inject
    ServiceRegistry2 serviceRegistry2;

}
