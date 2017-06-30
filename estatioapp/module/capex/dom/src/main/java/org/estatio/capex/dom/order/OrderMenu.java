package org.estatio.capex.dom.order;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "orders.OrderMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "100"
)
public class OrderMenu {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Order> allOrders(){
        return orderRepository.listAll();
    }


    @Action(semantics = SemanticsOf.SAFE)
    public List<Order> findOrderByDocumentName(final String nameOrBarcode){
        List <Order> result = new ArrayList<>();
        for (Document doc : incomingDocumentRepository.matchAllIncomingDocumentsByName(nameOrBarcode)){
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

    ///////////////////////////////////////////


    @Action(semantics = SemanticsOf.SAFE)
    public List<Order> findOrdersByOrderDate(final LocalDate fromDate, final LocalDate toDate) {
        return orderRepository.findByOrderDateBetween(fromDate, toDate);
    }

    public LocalDate default0FindOrdersByOrderDate() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindOrdersByOrderDate() {
        return clockService.now();
    }

    

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<Order> findOrdersByEntryDate(final LocalDate fromDate, final LocalDate toDate) {
        return orderRepository.findByEntryDateBetween(fromDate, toDate);
    }

    public LocalDate default0FindOrdersByEntryDate() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindOrdersByEntryDate() {
        return clockService.now();
    }

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<Order> findOrderByOrderNumber(final String orderNumber) {
        return orderRepository.matchByOrderNumber(orderNumber);
    }


    ///////////////////////////////////////////

    @Inject
    OrderRepository orderRepository;

    @Inject
    ClockService clockService;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    PaperclipRepository paperclipRepository;

}
