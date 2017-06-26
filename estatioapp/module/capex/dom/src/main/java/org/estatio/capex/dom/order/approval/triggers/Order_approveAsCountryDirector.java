package org.estatio.capex.dom.order.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;

@Mixin(method="act")
public class Order_approveAsCountryDirector extends
        Order_triggerAbstract {

    public Order_approveAsCountryDirector(Order order) {
        super(order, OrderApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR);
    }

}
