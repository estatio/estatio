package org.estatio.capex.dom.orderinvoice;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = OrderItemInvoiceItemLink.class, nature = NatureOfService.DOMAIN)
public class OrderItemInvoiceItemLinkRepository extends UdoDomainRepositoryAndFactory<OrderItemInvoiceItemLink> {

    public OrderItemInvoiceItemLinkRepository() {
        super(OrderItemInvoiceItemLinkRepository.class, OrderItemInvoiceItemLink.class);
    }

    public OrderItemInvoiceItemLink createBudgetCalculationResultLink(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem){

        OrderItemInvoiceItemLink budgetCalculationResultLink = newTransientInstance(OrderItemInvoiceItemLink.class);
        budgetCalculationResultLink.setOrderItem(orderItem);
        budgetCalculationResultLink.setInvoiceItem(invoiceItem);

        persist(budgetCalculationResultLink);

        return budgetCalculationResultLink;
    }

    public OrderItemInvoiceItemLink findOrCreateLink(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem) {
        return findUnique(orderItem, invoiceItem)  == null ?
                createBudgetCalculationResultLink(orderItem, invoiceItem) :
                findUnique(orderItem, invoiceItem);
    }

    public OrderItemInvoiceItemLink findUnique(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem) {
        return uniqueMatch("findUnique", "orderItem", orderItem, "invoiceItem", invoiceItem);
    }

    public List<OrderItemInvoiceItemLink> findByOrderItem(
            final OrderItem orderItem) {
        return allMatches("findByOrderItem", "orderItem", orderItem);
    }

    public List<OrderItemInvoiceItemLink> findByInvoiceItem(
            final IncomingInvoiceItem invoiceItem) {
        return allMatches("findByInvoiceItem", "invoiceItem", invoiceItem);
    }

    public List<OrderItemInvoiceItemLink> listAll(){
        return allInstances();
    }

}

