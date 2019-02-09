package org.estatio.module.order.dom.attr.prop;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttributeName;
import org.estatio.module.order.dom.attr.OrderAttributeRepository;

@Mixin(method="prop")
public class Order_attributeValueAbstract {
    private final Order order;
    private final OrderAttributeName attributeName;

    public Order_attributeValueAbstract(final Order order, final OrderAttributeName attributeName) {
        this.order = order;
        this.attributeName = attributeName;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @PropertyLayout(multiLine = Order.AttributeDescriptionType.Meta.MULTI_LINE)
    public String prop() {
        return orderAttributeRepository.findValueByOrderAndName(attributeName, order);
    }
    public boolean hideProp() {
        return false;
    }

    @Inject protected
    OrderAttributeRepository orderAttributeRepository;

}
