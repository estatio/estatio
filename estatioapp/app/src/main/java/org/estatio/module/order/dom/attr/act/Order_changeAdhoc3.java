package org.estatio.module.order.dom.attr.act;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttributeName;

@Mixin(method = "act")
public class Order_changeAdhoc3
                extends Order_changeAttributeAbstract {

    public Order_changeAdhoc3(final Order order) {
        super(order, OrderAttributeName.ORDER_CONFIRM_ADHOC_3);
    }
}
