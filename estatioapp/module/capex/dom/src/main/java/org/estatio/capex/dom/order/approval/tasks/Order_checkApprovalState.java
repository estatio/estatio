package org.estatio.capex.dom.order.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_checkStateAbstract;

@Mixin(method="act")
public class Order_checkApprovalState
        extends DomainObject_checkStateAbstract<
                    Order,
                    OrderApprovalStateTransition,
                    OrderApprovalStateTransitionType,
                    OrderApprovalState> {

    public Order_checkApprovalState(final Order order) {
        super(order, OrderApprovalStateTransition.class);
    }


}
