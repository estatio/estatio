package org.estatio.module.capex.app;

import java.math.BigInteger;
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
import org.apache.isis.applib.services.user.UserService;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.util.Mode;
import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.imports.OrderProjectImportAdapter;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "orders.OrderMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "65.1"
)
public class OrderMenu {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Order> allOrders(){
        return orderRepository.listAll();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Order createOrder(
            final Property property,
            final Project project,
            final Charge charge) {
        final String userAtPath = meService.me().getAtPath();
        return orderRepository.create(property, project, charge, userAtPath);
    }

    public boolean hideCreateOrder() {
        return !meService.me().getAtPath().startsWith("/ITA");
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

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    public Numerator createOrderNumberNumerator(final String format, final String atPath) {
        return numeratorRepository.findOrCreateNumerator(
                "Order number",
                null,
                format,
                BigInteger.ZERO,
                applicationTenancyRepository.findByPath(atPath));
    }

    public String validateCreateOrderNumberNumerator(final String format, final String atPath){
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser()) ? "You need administrator rights to create an order numerator" : null;
    }

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public List<Order> importOrdersItaly(final org.apache.isis.applib.value.Blob orderSheet){
        List<Order> result = new ArrayList<>();
        for (OrderProjectImportAdapter adapter : excelService.fromExcel(orderSheet, OrderProjectImportAdapter.class, "ECP Juma", Mode.RELAXED)){
            adapter.handle(null);
            if (adapter.deriverOrderNumber()!=null) {
                Order order = orderRepository.findByOrderNumber(adapter.deriverOrderNumber());
                if (order!=null && !result.contains(order)){
                    result.add(order);
                }
            }
        }
        return result;
    }

    @Inject
    OrderRepository orderRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    ClockService clockService;

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    UserService userService;

    @Inject
    MeService meService;

    @Inject ExcelService excelService;

}
