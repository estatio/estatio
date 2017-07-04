package org.estatio.capex.dom.order.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_nextTaskTransitionTypeAbstract;

@Mixin(method="prop")
public class Order_nextApprovalTaskTransitionType
        extends DomainObject_nextTaskTransitionTypeAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    public Order_nextApprovalTaskTransitionType(final Order order) {
        super(order, OrderApprovalStateTransition.class);
    }

    public boolean hideProp() {
        return super.hideProp();
    }

}
