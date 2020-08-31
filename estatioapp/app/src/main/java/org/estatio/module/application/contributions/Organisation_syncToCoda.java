package org.estatio.module.application.contributions;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

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
        return !isFrenchOrBelgiumSupplier();
    }

    protected boolean isFrenchOrBelgiumSupplier() {
        return isFranceOrBelgium() && orgIsSupplier();
    }

    protected boolean orgIsSupplier() {
        return organisation.hasPartyRoleType(IncomingInvoiceRoleTypeEnum.SUPPLIER);
    }

    protected boolean isFranceOrBelgium() {
        return organisation.getAtPath().startsWith("/BEL") || organisation.getAtPath().startsWith("/FRA");
    }

}
