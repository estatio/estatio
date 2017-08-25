package org.estatio.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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



    public void createLink(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem,
            final BigDecimal netAmount){

        OrderItemInvoiceItemLink orderItemInvoiceItemLink = newTransientInstance(OrderItemInvoiceItemLink.class);
        orderItemInvoiceItemLink.setOrderItem(orderItem);
        orderItemInvoiceItemLink.setInvoiceItem(invoiceItem);
        orderItemInvoiceItemLink.setNetAmount(netAmount);

        persist(orderItemInvoiceItemLink);

    }



    public void findOrCreateLink(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem,
            final BigDecimal netAmount) {
        if (findUnique(orderItem, invoiceItem) == null) {
            createLink(orderItem, invoiceItem, netAmount);
        } else {
            findUnique(orderItem, invoiceItem);
        }
    }

    public OrderItemInvoiceItemLink findUnique(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem) {
        return firstMatch("findUnique", "orderItem", orderItem, "invoiceItem", invoiceItem);
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




    public BigDecimal sumLinkNetAmountsByOrderItem(final OrderItem orderItem) {
        final List<OrderItemInvoiceItemLink> links = findByOrderItem(orderItem);
        return sum(links);
    }

    public BigDecimal sumLinkNetAmountsByInvoiceItem(final IncomingInvoiceItem invoiceItem) {
        final List<OrderItemInvoiceItemLink> links = findByInvoiceItem(invoiceItem);
        return sum(links);
    }

    private BigDecimal sum(final List<OrderItemInvoiceItemLink> links) {
        return links.stream()
                .filter(i->!i.getInvoiceItem().isDiscarded())
                .map(OrderItemInvoiceItemLink::getNetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    void removeLink(final OrderItemInvoiceItemLink link) {
        remove(link);
    }

}

