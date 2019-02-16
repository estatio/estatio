package org.estatio.module.capex.contributions;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.OrderAttribute;
import org.estatio.module.order.dom.attr.OrderAttributeRepository;

@Mixin(method = "act")
public class Order_initializePlaceholders {

    private final Order order;
    public Order_initializePlaceholders(final Order order) {
        this.order = order;
    }

    public Order act() throws IOException {
        orderAttributeRepository.initializeAttributes(order);
        return order;
    }

    public boolean hideAct() {
        final List<OrderAttribute> attributes = orderAttributeRepository.findByOrder(order);
        return !attributes.isEmpty();
    }

    @Inject
    OrderAttributeRepository orderAttributeRepository;
}
