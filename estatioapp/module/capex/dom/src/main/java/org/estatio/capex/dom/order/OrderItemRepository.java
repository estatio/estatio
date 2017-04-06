package org.estatio.capex.dom.order;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.time.CalendarType;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = OrderItem.class
)
public class OrderItemRepository {

    @Programmatic
    public OrderItem findByOrderAndCharge(final Order order, final IncomingCharge charge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByOrderAndCharge",
                        "order", order,
                        "charge", charge
                ));
    }


    @Programmatic
    public OrderItem create(
            final Order order,
            final IncomingCharge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final CalendarType calendarType) {
        final OrderItem orderItem = OrderItem.builder()
                .order(order)
                .charge(charge)
                .description(description)
                .netAmount(netAmount)
                .vatAmount(vatAmount)
                .grossAmount(grossAmount)
                .tax(tax)
                .calendarType(calendarType)
                .build();
        serviceRegistry2.injectServicesInto(orderItem);
        repositoryService.persistAndFlush(orderItem);
        return orderItem;
    }

    @Programmatic
    public OrderItem findOrCreate(
            final Order order,
            final IncomingCharge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final CalendarType calendarType) {
        OrderItem orderItem = findByOrderAndCharge(order, charge);
        if (orderItem == null) {
            orderItem = create(order, charge, description, netAmount, vatAmount, grossAmount, tax, calendarType);
        }
        return orderItem;
    }


    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
