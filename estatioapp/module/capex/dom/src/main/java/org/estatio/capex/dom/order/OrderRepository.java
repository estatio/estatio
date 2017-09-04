package org.estatio.capex.dom.order;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.numerator.dom.impl.Numerator;
import org.estatio.numerator.dom.impl.NumeratorRepository;

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
    public Order findByOrderNumberAndSellerAndOrderDate(final String orderNumber, final Party seller, final LocalDate orderDate){
        return repositoryService.firstMatch(
                new QueryDefault<>(
                        Order.class,
                        "findByOrderNumberAndSellerAndOrderDate",
                        "orderNumber", orderNumber,
                        "seller", seller,
                        "orderDate", orderDate));
    }

    @Programmatic
    public List<Order> findByOrderNumberAndSeller(final String orderNumber, final Party seller) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Order.class,
                        "findByOrderNumberAndSeller",
                        "orderNumber", orderNumber,
                        "seller", seller));
    }



    @Programmatic
    public List<Order> findByOrderDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Order.class,
                        "findByOrderDateBetween",
                        "fromDate", fromDate,
                        "toDate", toDate));
    }

    @Programmatic
    public List<Order> findByEntryDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Order.class,
                        "findByEntryDateBetween",
                        "fromDate", fromDate,
                        "toDate", toDate));
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
            final String orderNumber,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final OrderApprovalState approvalStateIfAny) {

        final Numerator numerator = numeratorRepository.findOrCreateNumerator(
                "Order number",
                null,
                "%05d",
                BigInteger.ZERO,
                applicationTenancyRepository.findByPath(atPath));

        final Order order = new Order(
                property,
                orderNumber == null ? numerator.nextIncrementStr() : orderNumber,
                sellerOrderReference,
                entryDate,
                orderDate,
                seller,
                buyer,
                atPath,
                approvalStateIfAny);
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
    public void delete(final Order order) {
        repositoryService.removeAndFlush(order);
    }

    @Programmatic
    public List<Order> findBySellerParty(final Party party) {
        if (party instanceof Organisation) {
            return findBySeller((Organisation) party);
        }
        return Lists.newArrayList();
    }

    @Programmatic
    public List<Order> findBySeller(final Organisation seller) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Order.class,
                        "findBySeller",
                        "seller", seller));
    }

    @Programmatic
    public List<Order> matchBySellerReferenceOrName(final String searchString) {
        List<Order> result = new ArrayList<>();
        for (Party party : partyRepository.findParties(searchString)){
            for (Order order : findBySellerParty(party)) {
                if (!result.contains(order)){
                    result.add(order);
                }
            }
        }
        return result;
    }

    @Programmatic
    public List<Order> findOrderByDocumentName(final String name){
        List <Order> result = new ArrayList<>();
        for (Document doc : incomingDocumentRepository.matchAllIncomingDocumentsByName(name)){
            for (Paperclip paperclip : paperclipRepository.findByDocument(doc)){
                if (paperclip.getAttachedTo().getClass().isAssignableFrom(Order.class)){
                    final Order attachedTo = (Order) paperclip.getAttachedTo();
                    if (!result.contains(attachedTo)) {
                        result.add(attachedTo);
                    }
                }
            }
        }
        return result;
    }

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject NumeratorRepository numeratorRepository;

    @Inject ApplicationTenancyRepository applicationTenancyRepository;

    @Inject PartyRepository partyRepository;



}
