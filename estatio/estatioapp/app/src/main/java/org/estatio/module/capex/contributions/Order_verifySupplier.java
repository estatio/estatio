package org.estatio.module.capex.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.app.user.MeService;

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
        if (meService.me().getAtPath().startsWith("/ITA")) {
            return true;
        }

        Organisation orgToVerify = (Organisation) order.getSeller();
        return orgToVerify==null || orgToVerify.isVerified();
    }

    @Inject
    private MeService meService;

}
