package org.estatio.capex.dom.order;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.capex.dom.order.OrderMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In"
)
public class OrderMenu {

    public List<Order> allOrders(){
        return orderRepository.listAll();

    }

    public Order newOrder(
            final String orderNumber,
            final String sellerOrderReference,
            final Party buyer,
            final Party seller,
            final LocalDate orderDate
    ){
        final String atPath = "/FRA";
        return orderRepository.create(orderNumber, sellerOrderReference, clockService.now(), orderDate,  seller, buyer, atPath, null, null);
    }

    @Inject
    OrderRepository orderRepository;

    @Inject
    ClockService clockService;
}
