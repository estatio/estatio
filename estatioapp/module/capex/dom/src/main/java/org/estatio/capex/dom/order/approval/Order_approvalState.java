package org.estatio.capex.dom.order.approval;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.state.DomainObject_currentStateAbstract;

@Mixin(method="prop")
public class Order_approvalState
        extends DomainObject_currentStateAbstract<
                        Order,
                        OrderApprovalStateTransition,
                        OrderApprovalStateTransitionType,
                        OrderApprovalState> {

    public Order_approvalState(final Order order) {
        super(order, OrderApprovalStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public OrderApprovalState prop() {
        return super.prop();
    }
}
