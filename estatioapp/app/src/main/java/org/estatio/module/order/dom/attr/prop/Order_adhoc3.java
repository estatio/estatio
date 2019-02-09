package org.estatio.module.order.dom.attr.prop;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttributeName;

@Mixin(method="prop")
public class Order_adhoc3
        extends Order_attributeValueAbstract {

    public Order_adhoc3(final Order order) {
        super(order, OrderAttributeName.ORDER_CONFIRM_ADHOC_3);
    }

}
