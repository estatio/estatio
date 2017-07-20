package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.Stateful;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "Order"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByOrderNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE orderNumber == :orderNumber "),
        @Query(
                name = "matchByOrderNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE orderNumber.matches(:orderNumber) "),
        @Query(
                name = "findByOrderDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE orderDate >= :fromDate "
                        + "   && orderDate <= :toDate "),
        @Query(
                name = "findByEntryDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE entryDate >= :fromDate "
                        + "   && entryDate <= :toDate "),
        @Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE seller == :seller ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "orders.Order",
        persistingLifecycleEvent = Order.ObjectPersistingEvent.class,
        persistedLifecycleEvent = Order.ObjectPersistedEvent.class,
        removingLifecycleEvent = Order.ObjectRemovingEvent.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Order extends UdoDomainObject2<Order> implements Stateful {

    public static class ObjectPersistedEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent <Order> {
    }
    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent <Order> {
    }
    public static class ObjectRemovingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectRemovingEvent <Order> {
    }

    public Order() {
        // TODO: may need to revise this when we know more...
        super("seller, orderDate, orderNumber, id");
    }

    public Order(
            final org.estatio.dom.asset.Property property,
            final String orderNumber,
            final String sellerOrderReference,
            final LocalDate entryDate,
            final LocalDate orderDate,
            final Party seller,
            final Party buyer,
            final String atPath,
            final OrderApprovalState approvalStateIfAny) {
        this();
        this.property = property;
        this.orderNumber = orderNumber;
        this.sellerOrderReference = sellerOrderReference;
        this.entryDate = entryDate;
        this.orderDate = orderDate;
        this.seller = seller;
        this.buyer = buyer;
        this.atPath = atPath;
        this.approvalState = approvalStateIfAny;
    }

    public String title() {

        final TitleBuffer buf = new TitleBuffer();

        final Optional<Document> document = lookupAttachedPdfService.lookupOrderPdfFrom(this);
        document.ifPresent(d -> buf.append(d.getName()));

        final Party seller = getSeller();
        if(seller != null) {
            buf.append(": ", seller);
        }

        final String orderNumber = getOrderNumber();
        if(orderNumber != null) {
            buf.append(", ", orderNumber);
        }

        return buf.toString();
    }

    /**
     * This relates to the owning property, while the child items may either also relate to the property,
     * or could potentially relate to individual units within the property.
     *
     * <p>
     *     This follows the same pattern as {@link IncomingInvoice}.
     * </p>
     */
    @javax.jdo.annotations.Column(name = "propertyId", allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private org.estatio.dom.asset.Property property;

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String orderNumber;

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String sellerOrderReference;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate entryDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate orderDate;

    @Column(allowsNull = "true", name = "sellerPartyId")
    @Getter @Setter
    private Party seller;

    @Column(allowsNull = "true", name = "buyerPartyId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Party buyer;

    @Persistent(mappedBy = "ordr", dependentElement = "true")
    @Getter @Setter
    private SortedSet<OrderItem> items = new TreeSet<>();

    @Mixin(method="act")
    public static class addItem {
        private final Order order;

        public addItem(final Order order) {
            this.order = order;
        }

        @MemberOrder(name="items", sequence = "1")
        public Order act(
                final Charge charge,
                @Nullable final String description,
                @Digits(integer=13, fraction = 2)
                final BigDecimal netAmount,
                @Nullable
                @Digits(integer=13, fraction = 2)
                final BigDecimal vatAmount,
                @Nullable
                @Digits(integer=13, fraction = 2)
                final BigDecimal grossAmount,
                @Nullable final Tax tax,
                @Nullable final String period,
                @Nullable final org.estatio.dom.asset.Property property,
                @Nullable final Project project,
                @Nullable final BudgetItem budgetItem
        ) {
            orderItemRepository.upsert(
                    order, charge, description, netAmount, vatAmount, grossAmount, tax, PeriodUtil.yearFromPeriod(period).startDate(), PeriodUtil.yearFromPeriod(period).endDate(), property, project, budgetItem);
            // (we think there's) no need to add to the getItems(), because the item points back to this order.
            return order;
        }

        public List<Charge> choices0Act(){
            List<Charge> result = chargeRepository.allIncoming();
            for (OrderItem item : order.getItems()) {
                if (item.getCharge()!=null && result.contains(item.getCharge())) {
                    result.remove(item.getCharge());
                }
            }
            return result;
        }

        public String default6Act(){
            return ofFirstItem(OrderItem::getStartDate)!=null ? PeriodUtil.periodFromInterval(new LocalDateInterval(ofFirstItem(OrderItem::getStartDate), ofFirstItem(OrderItem::getEndDate))) : null;
        }

        public org.estatio.dom.asset.Property default7Act(){
            return ofFirstItem(OrderItem::getProperty);
        }

        public Project default8Act(){
            return ofFirstItem(OrderItem::getProject);
        }

        public String validateAct(final Charge charge,
                final String description,
                final BigDecimal netAmount,
                final BigDecimal vatAmount,
                final BigDecimal grossAmount,
                final Tax tax,
                final String period,
                final org.estatio.dom.asset.Property property,
                final Project project,
                final BudgetItem budgetItem){
            if (period!=null && !period.equals("")) {
                return PeriodUtil.isValidPeriod(period) ? null : "Not a valid period";
            }
            return null;
        }

        public List<BudgetItem> choices9Act(
                final Charge charge,
                final String description,
                final BigDecimal netAmount,
                final BigDecimal vatAmount,
                final BigDecimal grossAmount,
                final Tax tax,
                final String period,
                final org.estatio.dom.asset.Property property,
                final Project project,
                final BudgetItem budgetItem) {

            return budgetItemChooser.choicesBudgetItemFor(property, charge);
        }

        public String disableAct() {
            return order.reasonDisabledDueToState();
        }

        private <T> T ofFirstItem(final Function<OrderItem, T> f) {
            final Optional<OrderItem> firstItemIfAny = firstItemIfAny();
            return firstItemIfAny.map(f).orElse(null);
        }

        private Optional<OrderItem> firstItemIfAny() {
            return  order.getItems().stream()
                    .filter(OrderItem.class::isInstance)
                    .map(OrderItem.class::cast)
                    .findFirst();
        }

        @Inject
        public OrderItemRepository orderItemRepository;

        @Inject
        BudgetItemChooser budgetItemChooser;

        @Inject
        ChargeRepository chargeRepository;

    }


    @Property(notPersisted = true)
    public BigDecimal getNetAmount() {
        return sum(OrderItem::getNetAmount);
    }

    @Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getVatAmount() {
        return sum(OrderItem::getVatAmount);
    }

    @Property(notPersisted = true)
    public BigDecimal getGrossAmount() {
        return sum(OrderItem::getGrossAmount);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> x) {
        return Lists.newArrayList(getItems()).stream()
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String atPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getAtPath());
    }


    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String approvedBy;

    public boolean hideApprovedBy() {
        return getApprovedBy() == null;
    }

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate approvedOn;

    public boolean hideApprovedOn() {
        return getApprovedOn() == null;
    }

    // TODO: this is inconsistent with IncomingInvoice; the database and this metadata should be changed to not-null.
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true")
    private OrderApprovalState approvalState;

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > S getStateOf(
            final Class<ST> stateTransitionClass) {
        if(stateTransitionClass == OrderApprovalStateTransition.class) {
            return (S) approvalState;
        }
        return null;
    }

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > void setStateOf(
            final Class<ST> stateTransitionClass, final S newState) {
        if(stateTransitionClass == OrderApprovalStateTransition.class) {
            setApprovalState( (OrderApprovalState) newState );
        }
    }

    @Programmatic
    public String reasonDisabledDueToState() {
        OrderApprovalState currentState = getApprovalState();
        return currentState == OrderApprovalState.NEW ?
                null :
                "Cannot modify because order is in state of " + currentState;
    }

    @Programmatic
    public boolean isImmutable(){
        return reasonDisabledDueToState()!=null;
    }

    @Programmatic
    public String reasonIncomplete(){
        StringBuffer buffer = new StringBuffer();
        if (getOrderNumber()==null){
            buffer.append("order number, ");
        }
        if (getBuyer()==null){
            buffer.append("buyer, ");
        }
        if (getSeller()==null){
            buffer.append("seller, ");
        }
        if (getNetAmount()==null){
            buffer.append("net amount, ");
        }
        if (getGrossAmount()==null){
            buffer.append("gross amount, ");
        }

        if (reasonItemsIncomplete()!=null){
            buffer.append(reasonItemsIncomplete());
        }

        final int buflen = buffer.length();
        return buflen != 0
                ? buffer.replace(buflen - 2, buflen, " required").toString()
                : null;
    }

    @Programmatic
    public String reasonItemsIncomplete(){
        StringBuffer buffer = new StringBuffer();
        for (OrderItem item : getItems()){
            if (item.reasonIncomplete()!=null) {
                buffer.append("(on item) ");
                buffer.append(item.reasonIncomplete());
            }
        }
        return buffer.length() == 0 ? null : buffer.toString();
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

}
