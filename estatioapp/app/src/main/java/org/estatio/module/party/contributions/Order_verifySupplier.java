package org.estatio.module.party.contributions;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;

@Mixin
public class Order_verifySupplier {

    private final Order order;

    public Order_verifySupplier(Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Order $$( final OrganisationNameNumberViewModel organisationCheck) {
        Organisation orgToVerify = (Organisation) order.getSeller();
        orgToVerify.verify(organisationCheck);
        return order;
    }

    public List<OrganisationNameNumberViewModel> choices0$$(){
        Organisation orgToVerify = (Organisation) order.getSeller();
        return orgToVerify.choices0Verify();
    }

    public boolean hide$$(){
        Organisation orgToVerify = (Organisation) order.getSeller();
        return orgToVerify==null || orgToVerify.isVerified();
    }

}
