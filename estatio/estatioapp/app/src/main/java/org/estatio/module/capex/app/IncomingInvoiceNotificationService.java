package org.estatio.module.capex.app;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceNotificationService {

    @Programmatic
    public IncomingInvoice findDuplicateInvoice(final IncomingInvoice incomingInvoice) {
        return queryResultsCache.execute(
                () -> incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate(incomingInvoice.getInvoiceNumber(), incomingInvoice.getSeller(), incomingInvoice.getInvoiceDate())
                , getClass(), "findDuplicateInvoice", incomingInvoice.getInvoiceNumber(), incomingInvoice.getSeller(), incomingInvoice.getInvoiceDate(), incomingInvoice);
    }

    @Programmatic
    public List<IncomingInvoice> findSimilarInvoices(final IncomingInvoice incomingInvoice) {
        return queryResultsCache.execute(
                () -> incomingInvoiceRepository.findByInvoiceNumberAndSeller(incomingInvoice.getInvoiceNumber(), incomingInvoice.getSeller())
                , getClass(), "findSimilarInvoices", incomingInvoice.getInvoiceNumber(), incomingInvoice.getSeller(), incomingInvoice);
    }

    @Programmatic
    public Party deriveBuyer(final IncomingInvoice incomingInvoice) {
        return queryResultsCache.execute(
                () -> buyerFinder.buyerDerivedFromDocumentName(incomingInvoice)
                , getClass(), "deriveBuyer", incomingInvoice);
    }

    @Programmatic
    public List<PaymentMethod> uniquePaymentMethodsForSeller(final IncomingInvoice incomingInvoice) {
        return queryResultsCache.execute(
                () -> incomingInvoiceRepository.findUniquePaymentMethodsForSeller(incomingInvoice.getSeller())
                , getClass(), "uniquePaymentMethodsForSeller", incomingInvoice.getSeller(), incomingInvoice);
    }

    @Programmatic
    public Optional<OrderItemInvoiceItemLink> findLinkForInvoiceItemIfAny(final IncomingInvoiceItem item) {
        return queryResultsCache.execute(
                () -> orderItemInvoiceItemLinkRepository.findByInvoiceItem(item)
                , getClass(), "findLinkForInvoiceItemIfAny", item);
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    BuyerFinder buyerFinder;

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
