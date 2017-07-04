package org.estatio.capex.dom.order.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_viewTransitionsAbstract;

@Mixin(method = "act")
public class Order_viewApprovalTransitions
        extends DomainObject_viewTransitionsAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    public Order_viewApprovalTransitions(final Order order) {
        super(order,
                OrderApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<OrderApprovalStateTransition> act() {
        return super.act();
    }

}
