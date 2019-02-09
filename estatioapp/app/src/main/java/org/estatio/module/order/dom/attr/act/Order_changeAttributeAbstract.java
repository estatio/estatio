package org.estatio.module.order.dom.attr.act;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.NotesType;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttributeName;
import org.estatio.module.order.dom.attr.OrderAttributeRepository;

public abstract class Order_changeAttributeAbstract {

    private final Order order;
    private final OrderAttributeName orderAttributeName;

    public Order_changeAttributeAbstract(final Order order, final OrderAttributeName orderAttributeName) {
        this.order = order;
        this.orderAttributeName = orderAttributeName;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Order act(
            @Parameter(maxLength = NotesType.Meta.MAX_LEN, optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = Order.DescriptionType.Meta.MULTI_LINE) final String overrideWith) {
        order.updateAttribute(this.orderAttributeName, overrideWith);
        return order;
    }

    public String disableAct() {
//        if (order.isImmutableDueToState()) {
//            return "Order can't be changed";
//        }
        return null;
    }

    public String default0Act() {
        return orderAttributeRepository.findValueByOrderAndName(orderAttributeName, order);
    }

    @Inject
    protected OrderAttributeRepository orderAttributeRepository;

}
