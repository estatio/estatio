package org.estatio.capex.dom.order;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.dom.asset.Property;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = OrderItem.class
)
public class OrderItemRepository {

    @Programmatic
    public OrderItem findByOrderAndIncomingCharge(final Order order, final IncomingCharge incomingCharge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByOrderAndIncomingCharge",
                        "order", order,
                        "incomingCharge", incomingCharge
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
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project) {
        final OrderItem orderItem = OrderItem.builder()
                .order(order)
                .charge(charge)
                .description(description)
                .netAmount(netAmount)
                .vatAmount(vatAmount)
                .grossAmount(grossAmount)
                .tax(tax)
                .startDate(startDate)
                .endDate(endDate)
                .property(property)
                .project(project)
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
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project
    ) {
        OrderItem orderItem = findByOrderAndIncomingCharge(order, charge);
        if (orderItem == null) {
            orderItem = create(order, charge, description, netAmount, vatAmount, grossAmount, tax, startDate, endDate,
                    property, project);
        }
        return orderItem;
    }


    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
