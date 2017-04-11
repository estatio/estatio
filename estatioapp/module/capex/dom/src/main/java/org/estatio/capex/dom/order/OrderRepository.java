package org.estatio.capex.dom.order;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

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
    public Order create(
            final String number,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final String approvedBy,
            final LocalDate approvedOn) {
        final Order order = Order.builder()
                .orderNumber(number)
                .sellerOrderReference(sellerOrderReference)
                .entryDate(entryDate)
                .orderDate(orderDate)
                .seller(seller)
                .buyer(buyer)
                .atPath(atPath)
                .approvedBy(approvedBy)
                .approvedOn(approvedOn)
                .build();
        serviceRegistry2.injectServicesInto(order);
        repositoryService.persist(order);
        return order;
    }

    @Programmatic
    public Order findOrCreate(
            final String number,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final String approvedBy,
            final LocalDate approvedOn) {
        Order order = findByOrderNumber(number);
        if (order == null) {
            order = create(number, sellerOrderReference, entryDate, orderDate,
                    seller, buyer, atPath, approvedBy, approvedOn);
        }
        return order;
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
