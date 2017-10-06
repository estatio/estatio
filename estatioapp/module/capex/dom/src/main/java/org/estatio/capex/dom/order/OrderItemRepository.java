package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Party;
import org.estatio.tax.dom.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = OrderItem.class
)
public class OrderItemRepository {

    @Programmatic
    public OrderItem findByOrderAndCharge(final Order order, final Charge charge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByOrderAndCharge",
                        "ordr", order,
                        "charge", charge
                ));
    }


    @Programmatic
    public OrderItem create(
            final Order order,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {
        final OrderItem orderItem =
                new OrderItem(
                        order,charge, description,
                        netAmount, vatAmount, grossAmount,
                        tax, startDate, endDate, property, project, budgetItem);
        serviceRegistry2.injectServicesInto(orderItem);
        repositoryService.persistAndFlush(orderItem);
        return orderItem;
    }

    @Programmatic
    public OrderItem upsert(
            final Order order,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {
        OrderItem orderItem = findByOrderAndCharge(order, charge);
        if (orderItem == null) {
            orderItem = create(order, charge, description, netAmount, vatAmount, grossAmount, tax, startDate, endDate,
                    property, project, budgetItem);
        } else {
            updateOrderItem(orderItem,
                    description,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    tax,
                    startDate,
                    endDate,
                    property,
                    project,
                    budgetItem);
        }
        return orderItem;
    }

    private void updateOrderItem(final OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem){
        orderItem.setDescription(description);
        orderItem.setNetAmount(netAmount);
        orderItem.setVatAmount(vatAmount);
        orderItem.setGrossAmount(grossAmount);
        orderItem.setTax(tax);
        orderItem.setStartDate(startDate);
        orderItem.setEndDate(endDate);
        orderItem.setProperty(property);
        orderItem.setProject(project);
        orderItem.setBudgetItem(budgetItem);
    }

    @Programmatic
    public void mergeItems(final OrderItem sourceItem, final OrderItem targetItem) {
        if (sourceItem==null || targetItem==null) return;
        if (sourceItem.equals(targetItem)) return;
        targetItem.addAmounts(sourceItem.getNetAmount(), sourceItem.getVatAmount(), sourceItem.getGrossAmount());
        sourceItem.removeItem();
    }

    @Programmatic
    public List<OrderItem> findByProjectAndCharge(final Project project, final Charge charge) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByProjectAndCharge",
                        "project", project,
                        "charge", charge
                ));
    }

    @Programmatic
    public List<OrderItem> findByCharge(final Charge charge) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByCharge",
                        "charge", charge
                ));
    }

    @Programmatic
    public List<OrderItem> matchByDescription(final String description) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex("*" + description + "*");
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "matchByDescription",
                        "description", pattern
                ));
    }

    @Programmatic
    public List<OrderItem> findByProject(final Project project) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByProject",
                        "project", project
                ));
    }

    @Programmatic
    public List<OrderItem> findByProperty(final Property property) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByProperty",
                        "property", property
                ));
    }

    @Programmatic
    public List<OrderItem> findByBudgetItem(final BudgetItem budgetItem) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findByBudgetItem",
                        "budgetItem", budgetItem
                ));
    }

    @Programmatic
    public List<OrderItem> findBySeller(final Party seller) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findBySeller",
                        "seller", seller
                ));
    }

    @Programmatic
    public List<OrderItem> findBySellerAndProperty(final Party seller, final Property property) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OrderItem.class,
                        "findBySellerAndProperty",
                        "seller", seller, "property", property
                ));
    }

    @Programmatic
    public List<OrderItem> listAll() {
        return repositoryService.allInstances(OrderItem.class);
    }

    @Programmatic
    public List<OrderItem> orderItemsNotOnProjectItem(final Project project){
        List<Charge> chargesOnProject = Lists.newArrayList(project.getItems()).stream().map(x->x.getCharge()).collect(Collectors.toList());
        return findByProject(project).stream().filter(x->!chargesOnProject.contains(x.getCharge())).collect(Collectors.toList());
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;

}
