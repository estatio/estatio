package org.estatio.module.order.dom.attr.act;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttributeName;

@Mixin(method = "act")
public class Order_changeSignature
                extends Order_changeAttributeAbstract {

    public Order_changeSignature(final Order order) {
        super(order, OrderAttributeName.ORDER_CONFIRM_SIGNATURE);
    }

}
