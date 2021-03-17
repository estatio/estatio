package org.estatio.module.capex.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.isisaddons.module.security.app.user.MeService;

import org.estatio.module.capex.dom.order.Order;

@Mixin
public class Order_retrieveCodaSupplier {

    private final Order order;

    public Order_retrieveCodaSupplier(Order order) {
        this.order = order;
    }

    @Action()
    public Order $$(final String supplierReference) {
        // TODO: use party ref to retrieve from Coda
        return order;
    }

    public boolean hide$$() {
        return !meService.me().getAtPath().startsWith("/ITA");
    }

    @Inject
    private MeService meService;
}
