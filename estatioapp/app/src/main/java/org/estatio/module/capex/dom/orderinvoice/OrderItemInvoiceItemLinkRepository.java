package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = OrderItemInvoiceItemLink.class, nature = NatureOfService.DOMAIN)
public class OrderItemInvoiceItemLinkRepository extends UdoDomainRepositoryAndFactory<OrderItemInvoiceItemLink> {

    public OrderItemInvoiceItemLinkRepository() {
        super(OrderItemInvoiceItemLinkRepository.class, OrderItemInvoiceItemLink.class);
    }


    @Programmatic
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



    @Programmatic
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

    @Programmatic
    public OrderItemInvoiceItemLink findUnique(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem) {
        return firstMatch("findUnique", "orderItem", orderItem, "invoiceItem", invoiceItem);
    }

    @Programmatic
    public List<OrderItemInvoiceItemLink> findByInvoice(final IncomingInvoice incomingInvoice) {
        return allMatches("findByInvoice", "invoice", incomingInvoice);
    }

    @Programmatic
    public List<OrderItemInvoiceItemLink> findByOrder(final Order order) {
        return allMatches("findByOrder", "order", order);
    }

    @Programmatic
    public List<OrderItemInvoiceItemLink> findByOrderItem(
            final OrderItem orderItem) {
        return allMatches("findByOrderItem", "orderItem", orderItem);
    }

    @Programmatic
    public List<IncomingInvoiceItem> findLinkedInvoiceItemsByOrderItem(final OrderItem orderItem) {
        return findLinkedInvoiceItemsByOrderItemAsStream(orderItem)
                .collect(Collectors.toList());
    }

    @Programmatic
    public Stream<IncomingInvoiceItem> findLinkedInvoiceItemsByOrderItemAsStream(final OrderItem orderItem) {
        return findByOrderItem(orderItem).stream()
                .map(OrderItemInvoiceItemLink::getInvoiceItem);
    }

    @Programmatic
    public Optional<OrderItemInvoiceItemLink> findByInvoiceItem(
            final IncomingInvoiceItem invoiceItem) {
        return Optional.ofNullable(firstMatch("findByInvoiceItem", "invoiceItem", invoiceItem));
    }


    @Programmatic
    public Optional<OrderItem> findLinkedOrderItemsByInvoiceItem(final IncomingInvoiceItem invoiceItem) {
        final Optional<OrderItemInvoiceItemLink> linkIfAny = findByInvoiceItem(invoiceItem);
        return linkIfAny.map(OrderItemInvoiceItemLink::getOrderItem);
    }

    @Programmatic
    public List<OrderItemInvoiceItemLink> listAll(){
        return allInstances();
    }


    @Programmatic
    public BigDecimal calculateNetAmountLinkedToInvoice(final IncomingInvoice incomingInvoice) {
        final List<OrderItemInvoiceItemLink> links = findByInvoice(incomingInvoice);
        return sum(links);
    }

    @Programmatic
    public BigDecimal calculateNetAmountLinkedToOrder(final Order order) {
        final List<OrderItemInvoiceItemLink> links = findByOrder(order);
        return sum(links);
    }

    @Programmatic
    public BigDecimal calculateNetAmountLinkedToOrderItem(final OrderItem orderItem) {
        final List<OrderItemInvoiceItemLink> links = findByOrderItem(orderItem);
        return sum(links);
    }

    @Programmatic
    public BigDecimal calculateNetAmountLinkedFromInvoiceItem(final IncomingInvoiceItem invoiceItem) {
        final Optional<OrderItemInvoiceItemLink> links = findByInvoiceItem(invoiceItem);
        return sum(asStream(links));
    }

    private static <T> Stream<T> asStream(final Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    BigDecimal calculateNetAmountNotLinkedFromInvoiceItem(final IncomingInvoiceItem invoiceItem) {
        final BigDecimal netAmount = calculateNetAmountLinkedFromInvoiceItem(invoiceItem);
        return invoiceItem.getNetAmount()!=null ? invoiceItem.getNetAmount().subtract(netAmount) : BigDecimal.ZERO;
    }

    BigDecimal sum(final List<OrderItemInvoiceItemLink> links) {
        return sum(links.stream());
    }
    BigDecimal sum(final Stream<OrderItemInvoiceItemLink> links) {
        return links
                .filter(i->!i.getInvoiceItem().isDiscarded())
                .map(OrderItemInvoiceItemLink::getNetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    void removeLink(final OrderItemInvoiceItemLink link) {
        remove(link);
    }

}

