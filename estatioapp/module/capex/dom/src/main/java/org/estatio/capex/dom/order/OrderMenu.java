package org.estatio.capex.dom.order;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.PartyRepository;

import lombok.Getter;
import lombok.Setter;

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
    public List<Order> findOrder(
            @Nullable final String barcode,
            @Nullable final String sellerNameOrReference,
            @ParameterLayout(named = "Order Date (Approximately)")
            @Nullable final LocalDate orderDate
    ){
        return new OrderMenu.OrderFinder(orderRepository, partyRepository)
                .filterOrFindByDocumentName(barcode)
                .filterOrFindBySeller(sellerNameOrReference)
                .filterOrFindByOrderDate(orderDate)
                .getResult();
    }

    public String validateFindOrder(final String barcode, final String sellerNameOfReference, final LocalDate orderDate){
        if (barcode!=null && barcode.length()<3){
            return "Give at least 3 characters for barcode (document name)";
        }
        if (sellerNameOfReference!=null && sellerNameOfReference.length()<3){
            return "Give at least 3 characters for seller name or reference";
        }
        return null;
    }

    static class OrderFinder {

        public OrderFinder(OrderRepository orderRepository, PartyRepository partyRepository){
            this.result = new ArrayList<>();
            this.orderRepository = orderRepository;
            this.partyRepository = partyRepository;
        }

        @Getter @Setter
        List<Order> result;

        OrderRepository orderRepository;

        PartyRepository partyRepository;


        OrderMenu.OrderFinder filterOrFindByDocumentName(final String barcode){
            if (barcode==null) return this;

            List<Order> resultsForBarcode = orderRepository.findOrderByDocumentName(barcode);
            if (!this.result.isEmpty()){
                filterByDocumentNameResults(resultsForBarcode);
            } else {
                setResult(resultsForBarcode);
            }
            return this;
        }

        OrderMenu.OrderFinder filterOrFindBySeller(final String sellerNameOrReference){
            if (sellerNameOrReference==null || sellerNameOrReference.equals("")) return this;

            List<Organisation> sellerCandidates =
                    partyRepository.findParties("*".concat(sellerNameOrReference).concat("*"))
                            .stream()
                            .filter(Organisation.class::isInstance)
                            .map(Organisation.class::cast)
                            .collect(Collectors.toList());

            if (!this.result.isEmpty()) {
                filterBySellerCandidates(sellerCandidates);
            } else {
                createResultForSellerCandidates(sellerCandidates);
            }

            return this;
        }

        OrderMenu.OrderFinder filterOrFindByOrderDate(final LocalDate orderDate){
            if (orderDate==null) return this;
            LocalDate orderDateStart = orderDate.minusDays(5);
            LocalDate orderDateEnd = orderDate.plusDays(5);
            if (!this.result.isEmpty()){
                filterByOrderDate(orderDateStart, orderDateEnd);
            } else {
                createResultForOrderDate(orderDateStart, orderDateEnd);
            }
            return this;
        }

        void filterByDocumentNameResults(List<Order> resultsForBarcode){
            setResult(
                    this.result
                            .stream()
                            .filter(x->resultsForBarcode.contains(x))
                            .collect(Collectors.toList())
            );
        }

        void filterBySellerCandidates(final List<Organisation> sellerCandidates){
            if (sellerCandidates.isEmpty()){
                // reset result
                this.result = new ArrayList<>();
            } else {
                // filter result
                Predicate<Order> isInSellerCandidatesList =
                        x->sellerCandidates.contains(x.getSeller());
                setResult(result.stream().filter(isInSellerCandidatesList).collect(Collectors.toList()));
            }
        }

        void createResultForSellerCandidates(final List<Organisation> sellerCandidates){
            for (Organisation candidate : sellerCandidates) {
                this.result.addAll(
                        orderRepository.findBySeller(candidate)
                                .stream()
                                .collect(Collectors.toList())
                );
            }
        }

        void filterByOrderDate(final LocalDate orderDateStart, final LocalDate orderDateEnd){
            Predicate<Order> hasOrderDate = x->x.getOrderDate()!=null;
            Predicate<Order> orderDateInInterval = x->new LocalDateInterval(orderDateStart, orderDateEnd).contains(x.getOrderDate());
            setResult(
                    this.result
                            .stream()
                            .filter(hasOrderDate)
                            .filter(orderDateInInterval)
                            .collect(Collectors.toList())
            );
        }

        void createResultForOrderDate(final LocalDate orderDateStart, final LocalDate orderDateEnd){
            Predicate<Order> hasOrderDate = x->x.getOrderDate()!=null;
            Predicate<Order> orderDateInInterval = x->new LocalDateInterval(orderDateStart, orderDateEnd).contains(x.getOrderDate());
            setResult(
                    orderRepository.listAll()
                            .stream()
                            .filter(hasOrderDate)
                            .filter(orderDateInInterval)
                            .collect(Collectors.toList())
            );
        }

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
    PartyRepository partyRepository;

    @Inject
    ClockService clockService;

}
