package org.estatio.module.application.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.application.app.AdministrationMenu;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;

@Mixin(method = "act")
public class Organisation_syncToCoda {

    private final Organisation organisation;

    public Organisation_syncToCoda(final Organisation organisation) {
        this.organisation = organisation;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Organisation act() {
        return organisation;
    }

    public boolean hideAct() {
        return (!organisation.getAtPath().startsWith("/BEL") && !organisation.getAtPath().startsWith("/FRA")) || !organisation.hasPartyRoleType(IncomingInvoiceRoleTypeEnum.SUPPLIER);
    }

    @Inject
    WrapperFactory wrapperFactory;

    @Inject
    AdministrationMenu administrationMenu;
}
