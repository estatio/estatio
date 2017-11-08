package org.estatio.module.capex.dom.order.approval.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.dobj.DomainObject_transitionsAbstract;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "coll")
public class Order_approvalTransitions
        extends DomainObject_transitionsAbstract<
                                Order,
                                OrderApprovalStateTransition,
                                OrderApprovalStateTransitionType,
                                OrderApprovalState> {

    public Order_approvalTransitions(final Order order) {
        super(order,
                OrderApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<OrderApprovalStateTransition> coll() {
        return super.coll();
    }

}
