package org.estatio.capex.dom.order;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.dom.asset.Property;
import org.estatio.dom.project.Project;

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
    public Order findByReference(final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Order.class,
                        "findByReference",
                        "reference", reference));
    }

    @Programmatic
    public Order create(
            final String reference,
            final String number,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final TimeInterval period,
            final String sellerName,
            final Project project,
            final Property property,
            final String atPath,
            final String approvedBy,
            final LocalDate approvedOn) {
        final Order order = Order.builder()
                .reference(reference)
                .number(number)
                .entryDate(entryDate)
                .orderDate(orderDate)
                .period(period)
                .sellerName(sellerName)
                .project(project)
                .property(property)
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
            final String reference,
            final String number,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final TimeInterval period,
            final String sellerName,
            final Project project,
            final Property property,
            final String atPath,
            final String approvedBy,
            final LocalDate approvedOn) {
        Order order = findByReference(reference);
        if (order == null) {
            order = create(reference, number, entryDate, orderDate,
                    period, sellerName, project, property, atPath,
                    approvedBy, approvedOn);
        }
        return order;
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
