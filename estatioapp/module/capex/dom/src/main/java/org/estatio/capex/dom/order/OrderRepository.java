package org.estatio.capex.dom.order;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Order.class
)
public class OrderRepository {

    @Programmatic
    public java.util.List<Order> listAll() {
        return repositoryService.allInstances(Order.class);
    }

    @Programmatic
    public Order findByOrderNumber(final String orderNumber) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Order.class,
                        "findByOrderNumber",
                        "orderNumber", orderNumber));
    }

    @Programmatic
    public List<Order> matchByOrderNumber(final String orderNumber) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex("*" + orderNumber + "*");
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Order.class,
                        "matchByOrderNumber",
                        "orderNumber", pattern));
    }

    @Programmatic
    public Order create(
            final Property property,
            final String number,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final OrderApprovalState approvalStateIfAny) {
        final Order order = new Order(
                property, number, sellerOrderReference, entryDate, orderDate, seller, buyer, atPath, approvalStateIfAny);
        serviceRegistry2.injectServicesInto(order);
        repositoryService.persistAndFlush(order);
        return order;
    }

    @Programmatic
    public Order upsert(
            final Property property,
            final String number,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final OrderApprovalState approvalStateIfAny) {
        Order order = findByOrderNumber(number);
        if (order == null) {
            order = create(property, number, sellerOrderReference, entryDate, orderDate,
                    seller, buyer, atPath, approvalStateIfAny);
        } else {
            updateOrder(
                    order,
                    property,
                    sellerOrderReference,
                    entryDate, orderDate,
                    seller,
                    buyer,
                    atPath
            );
        }
        return order;
    }

    private void updateOrder(
            final Order order,
            final Property property,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath
    ){
        order.setProperty(property);
        order.setSellerOrderReference(sellerOrderReference);
        order.setEntryDate(entryDate);
        order.setOrderDate(orderDate);
        order.setSeller(seller);
        order.setBuyer(buyer);
        order.setAtPath(atPath);
    }

    @Programmatic
    public List<Order> findBySeller(final Organisation seller) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Order.class,
                        "findBySeller",
                        "seller", seller));
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;

}
