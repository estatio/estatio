package org.estatio.module.order.dom.attr.prop;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttributeName;

@Mixin(method="prop")
public class Order_workSchedule
        extends Order_attributeValueAbstract {

    public Order_workSchedule(final Order order) {
        super(order, OrderAttributeName.CONFIRMATION_WORK_SCHEDULE);
    }

}
