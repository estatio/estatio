package org.estatio.module.capex.contributions;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.mixins.T_preview;

import org.estatio.module.capex.dom.order.Order;

@Mixin(method = "act")
public class Order_previewDocument extends T_preview<Order> {

    public Order_previewDocument(final Order order) {
        super(order);
    }

}
