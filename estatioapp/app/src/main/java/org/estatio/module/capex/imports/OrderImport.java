package org.estatio.module.capex.imports;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.OrderImport"
)
public class OrderImport implements FixtureAwareRowHandler<OrderImport>, ExcelFixtureRowHandler, Importable {

    public String title() {
        return "order import";
    }

    public OrderImport() {
    }

    public OrderImport(
            final String orderPropertyReference,
            final String orderType,
            final String orderNumber,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final String sellerReference,
            final String buyerReference,
            final String atPath,
            final String approvalStateIfAny,
            final String approvedBy,
            final LocalDate approvedOn,
            final String chargeReference,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final LocalDate startDate,
            final LocalDate endDate,
            final String itemPropertyReference,
            final String projectReference
    ) {
        this();
        this.orderPropertyReference = orderPropertyReference;
        this.orderType = orderType;
        this.orderNumber = orderNumber;
        this.sellerOrderReference = sellerOrderReference;
        this.entryDate = entryDate;
        this.orderDate = orderDate;
        this.sellerReference = sellerReference;
        this.buyerReference = buyerReference;
        this.atPath = atPath;
        this.approvalStateIfAny = approvalStateIfAny;
        this.approvedBy = approvedBy;
        this.approvedOn = approvedOn;
        this.chargeReference = chargeReference;
        this.description = description;
        this.netAmount = netAmount;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.itemPropertyReference = itemPropertyReference;
        this.projectReference = projectReference;
    }

    // order
    @Getter @Setter
    private String orderPropertyReference;
    @Getter @Setter
    private String orderType;
    @Getter @Setter
    private String orderNumber;
    @Getter @Setter
    private String sellerOrderReference;
    @Getter @Setter
    private LocalDate entryDate;
    @Getter @Setter
    private LocalDate orderDate;
    @Getter @Setter
    private String sellerReference;
    @Getter @Setter
    private String buyerReference;
    @Getter @Setter
    private String atPath;
    @Getter @Setter
    private String approvalStateIfAny;
    @Getter @Setter
    private String approvedBy;
    @Getter @Setter
    private LocalDate approvedOn;

    // order item
    @Getter @Setter
    private String chargeReference;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private BigDecimal netAmount;
    @Getter @Setter
    private BigDecimal vatAmount;
    @Getter @Setter
    private BigDecimal grossAmount;
    @Getter @Setter
    private LocalDate startDate;
    @Getter @Setter
    private LocalDate endDate;
    @Getter @Setter
    private String itemPropertyReference;
    @Getter @Setter
    private String projectReference;


    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    @Override
    @Programmatic
    public void handleRow(final OrderImport previousRow) {
        importData(previousRow);
    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);

    }

    @Override
    @Programmatic
    public List<Object> importData(Object previousRow) {
        Order order = upsertOrder();
        OrderItem orderItem = findOrCreateOrderItem(order);
        return Lists.newArrayList(orderItem);
    }

    private Order upsertOrder() {
            Property property = propertyRepository.findPropertyByReference(getOrderPropertyReference());
            IncomingInvoiceType orderType = getOrderType() != null ? IncomingInvoiceType.valueOf(getOrderType()) : null;
            Organisation seller = (Organisation) partyRepository.findPartyByReference(getSellerReference());
            Organisation buyer = (Organisation) partyRepository.findPartyByReference(getBuyerReference());
            OrderApprovalState approvalState = getApprovalStateIfAny() != null ? OrderApprovalState.valueOf(getApprovalStateIfAny()) : null;
            Order order = orderRepository.upsert(property, orderType, getOrderNumber(), getSellerOrderReference(), getEntryDate(), getOrderDate(), seller, buyer, "/ITA", approvalState);
            order.setApprovedBy(getApprovedBy());
            order.setApprovedOn(getApprovedOn());
            createTransitionsIfNotAlready(order);
        return order;
    }

    private OrderItem findOrCreateOrderItem(final Order order) {
        return orderItemRepository.upsert(
                order,
                chargeRepository.findByReference(getChargeReference()),
                getDescription(),
                getNetAmount(),
                getVatAmount(),
                getGrossAmount(),
                null,
                getStartDate(),
                getEndDate(),
                propertyRepository.findPropertyByReference(getItemPropertyReference()),
                projectRepository.findByReference(getProjectReference()),
                null
        );
    }

    private void createTransitionsIfNotAlready(final Order order){
        final List<OrderApprovalStateTransition> stateTransitions = stateTransitionRepository.findByDomainObject(order);
        final List<OrderApprovalStateTransition> instantiationTransitions = stateTransitions.stream()
                    .filter(t->t.getTransitionType()==OrderApprovalStateTransitionType.INSTANTIATE)
                    .collect(Collectors.toList());
        final List<OrderApprovalStateTransition> completionTransitions = stateTransitions.stream()
                .filter(t->t.getTransitionType()==OrderApprovalStateTransitionType.COMPLETE_WITH_APPROVAL)
                .collect(Collectors.toList());

        if (instantiationTransitions.isEmpty()){
            OrderApprovalStateTransition instTrans = stateTransitionRepository.create(order, OrderApprovalStateTransitionType.INSTANTIATE,null,null,null,null);
            instTrans.setToState(OrderApprovalState.NEW);
            instTrans.setCompleted(true);
            instTrans.setCompletedBy("import");
            instTrans.setComment("import");
        }
        if (order.getApprovalState().equals(OrderApprovalState.APPROVED) && completionTransitions.isEmpty()){
            OrderApprovalStateTransition complTrans =stateTransitionRepository.create(order, OrderApprovalStateTransitionType.COMPLETE_WITH_APPROVAL,OrderApprovalState.NEW,null,null,null);
            complTrans.setFromState(OrderApprovalState.NEW);
            complTrans.setToState(OrderApprovalState.APPROVED);
            complTrans.setCompleted(true);
            complTrans.setCompletedBy("import");
            complTrans.setComment("import");
        }

    }

    @Inject PartyRepository partyRepository;

    @Inject PropertyRepository propertyRepository;

    @Inject OrderRepository orderRepository;

    @Inject ChargeRepository chargeRepository;

    @Inject OrderItemRepository orderItemRepository;

    @Inject ProjectRepository projectRepository;

    @Inject
    OrderApprovalStateTransition.Repository stateTransitionRepository;

}
