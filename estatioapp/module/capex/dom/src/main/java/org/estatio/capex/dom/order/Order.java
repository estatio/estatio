package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
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
import org.estatio.module.bankaccount.dom.BankAccountRepository;
import org.estatio.module.bankaccount.dom.utils.IBANValidator;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.tax.dom.Tax;

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
                name = "findBySellerOrderReferenceAndSeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE sellerOrderReference == :sellerOrderReference "
                        + "   && seller == :seller "),
        @Query(
                name = "findBySellerOrderReferenceAndSellerAndOrderDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.Order "
                        + "WHERE sellerOrderReference == :sellerOrderReference "
                        + "   && seller == :seller "
                        + "   && orderDate == :orderDate "),
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
@Indices({
        @Index(name = "Order_sellerOrderReference_IDX", members = { "sellerOrderReference" })
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
    @Getter @Setter
    private org.estatio.dom.asset.Property property;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editProperty(
            @Nullable
            final org.estatio.dom.asset.Property property,
            final boolean changeOnItemsAsWell){
        setProperty(property);
        if (changeOnItemsAsWell){
            com.google.common.collect.Lists.newArrayList(getItems())  // eagerly load (DN 4.x collections do not support streaming)
                    .stream()
                    .map(OrderItem.class::cast)
                    .forEach(x->x.setProperty(property));
        }
        return this;
    }

    public org.estatio.dom.asset.Property default0EditProperty(){
        return getProperty();
    }

    public boolean default1EditProperty(){
        return true;
    }

    public String disableEditProperty(){
        if (isImmutable()){
            return orderImmutableReason();
        }
        return propertyIsImmutableReason();
    }

    private String propertyIsImmutableReason(){
        for (OrderItem item : getItems()){
            if (item.isLinkedToInvoiceItem()){
                return "Property cannot be changed because an item is linked to an invoice";
            }
        }
        return null;
    }

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String orderNumber;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editOrderNumber(
            @Nullable
            final String orderNumber){
        setOrderNumber(orderNumber);
        return this;
    }

    public String default0EditOrderNumber(){
        return getOrderNumber();
    }

    public String disableEditOrderNumber(){
        return orderImmutableReason();
    }

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    @PropertyLayout(named = "Supplier order ref.")
    private String sellerOrderReference;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editSellerOrderReference(
            @Nullable
            final String sellerOrderReference){
        setSellerOrderReference(sellerOrderReference);
        return this;
    }

    public String default0EditSellerOrderReference(){
        return getSellerOrderReference();
    }

    public String disableEditSellerOrderReference(){
        return orderImmutableReason();
    }

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate entryDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate orderDate;

    public Order changeDates(
            @Nullable
            final LocalDate orderDate,
            @Nullable
            final LocalDate entryDate
    ){
        setOrderDate(orderDate);
        setEntryDate(entryDate);
        return this;
    }

    public LocalDate default0ChangeDates(){
        return getOrderDate();
    }

    public LocalDate default1ChangeDates(){
        return getEntryDate();
    }

    public String disableChangeDates(){
        return orderImmutableReason();
    }

    @Column(allowsNull = "true", name = "sellerPartyId")
    @PropertyLayout(named = "Supplier")
    @Getter @Setter
    private Party seller;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit Supplier")
    public Order editSeller(
            @Nullable
            final Party supplier,
            final boolean createRoleIfRequired){
        setSeller(supplier);
        if(supplier != null && createRoleIfRequired) {
            partyRoleRepository.findOrCreate(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return this;
    }

    public String validateEditSeller(final Party party, final boolean createRoleIfRequired){
        if(party != null && !createRoleIfRequired) {
            // requires that the supplier already has this role
            return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return null;
    }

    public Party default0EditSeller(){
        return getSeller();
    }

    public String disableEditSeller(){
        if (isImmutable()){
            return orderImmutableReason();
        }
        return sellerIsImmutableReason();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public Order createSeller(
            @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION)
            @Nullable
            final String reference,
            final boolean useNumeratorForReference,
            final String name,
            final Country country,
            @Nullable
            final String ibanNumber) {
        Organisation organisation = organisationRepository
                .newOrganisation(reference, useNumeratorForReference, name, country);
        setSeller(organisation);
        if (ibanNumber != null) {
            bankAccountRepository.newBankAccount(organisation, ibanNumber, null);
        }
        partyRoleRepository.findOrCreate(organisation, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        return this;
    }

    public String validateCreateSeller(
            final String reference,
            final boolean useNumeratorForReference,
            final String name,
            final Country country,
            final String ibanNumber){
        if (ibanNumber != null && !IBANValidator.valid(ibanNumber)){
            return String.format("%s is not a valid iban number", ibanNumber);
        }
        return null;
    }

    public String disableCreateSeller(){
        if (isImmutable()){
            return orderImmutableReason();
        }
        return sellerIsImmutableReason();
    }

    private String sellerIsImmutableReason(){
        for (OrderItem item : getItems()){
            if (item.isLinkedToInvoiceItem()){
                return "Seller cannot be changed because an item is linked to an invoice";
            }
        }
        return null;
    }

    @Column(allowsNull = "true", name = "buyerPartyId")
    @PropertyLayout(named = "ECP (as buyer)", hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Party buyer;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit ECP (as buyer)")
    public Order editBuyer(
            @Nullable
            final Party buyer){
        setBuyer(buyer);
        return this;
    }

    public List<Party> autoComplete0EditBuyer(@MinLength(3) final String searchPhrase){
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public String validate0EditBuyer(final Party party){
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }


    public Party default0EditBuyer(){
        return getBuyer();
    }

    public String disableEditBuyer(){
        return orderImmutableReason();
    }

    @Persistent(mappedBy = "ordr", dependentElement = "true")
    @Getter @Setter
    private SortedSet<OrderItem> items = new TreeSet<>();

    @MemberOrder(name="items", sequence = "1")
    public Order addItem(
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
                this, charge, description, netAmount, vatAmount, grossAmount, tax, PeriodUtil.yearFromPeriod(period).startDate(), PeriodUtil.yearFromPeriod(period).endDate(), property, project, budgetItem);
        // (we think there's) no need to add to the getItems(), because the item points back to this order.
        return this;
    }

    public List<Charge> choices0AddItem(){
        List<Charge> result = chargeRepository.allIncoming();
        for (OrderItem item : getItems()) {
            if (item.getCharge()!=null && result.contains(item.getCharge())) {
                result.remove(item.getCharge());
            }
        }
        return result;
    }

    public String default6AddItem(){
        return ofFirstItem(OrderItem::getStartDate)!=null ? PeriodUtil.periodFromInterval(new LocalDateInterval(ofFirstItem(OrderItem::getStartDate), ofFirstItem(OrderItem::getEndDate))) : null;
    }

    public org.estatio.dom.asset.Property default7AddItem(){
        return ofFirstItem(OrderItem::getProperty);
    }

    public Project default8AddItem(){
        return ofFirstItem(OrderItem::getProject);
    }

    public String validateAddItem(final Charge charge,
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

    public List<BudgetItem> choices9AddItem(
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

    public String disableAddItem() {
        return orderImmutableReason();
    }

    public boolean hideAddItem() {
        return getItems().isEmpty();
    }

    @MemberOrder(name = "items", sequence = "2")
    public Order splitItem(
            final OrderItem itemToSplit,
            final String newItemDescription,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal newItemNetAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2)
            final BigDecimal newItemVatAmount,
            @Nullable
            final Tax newItemtax,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            @Nullable
            final org.estatio.dom.asset.Property newItemProperty,
            @Nullable
            final Project newItemProject,
            @Nullable
            final BudgetItem newItemBudgetItem,
            @Nullable
            final String newItemPeriod
    ) {
        itemToSplit.subtractAmounts(newItemNetAmount, newItemVatAmount, newItemGrossAmount);
        orderItemRepository.upsert(
                this,
                newItemCharge,
                newItemDescription,
                newItemNetAmount,
                newItemVatAmount,
                newItemGrossAmount,
                newItemtax,
                newItemPeriod != null ? PeriodUtil.yearFromPeriod(newItemPeriod).startDate() : null,
                newItemPeriod != null ? PeriodUtil.yearFromPeriod(newItemPeriod).endDate() : null,
                newItemProperty,
                newItemProject,
                newItemBudgetItem
        );
        return this;
    }

    public boolean hideSplitItem(){
        return getItems().isEmpty();
    }

    public OrderItem default0SplitItem() {
        return firstItemIfAny()!=null ? getItems().first() : null;
    }

    public Tax default4SplitItem() {
        return ofFirstItem(OrderItem::getTax);
    }

    public org.estatio.dom.asset.Property default7SplitItem() {
        return getProperty();
    }

    public Project default8SplitItem() {
        return ofFirstItem(OrderItem::getProject);
    }

    public BudgetItem default9SplitItem() {
        return ofFirstItem(OrderItem::getBudgetItem);
    }

    public String default10SplitItem() {
        return ofFirstItem(OrderItem::getPeriod);
    }

    public List<OrderItem> choices0SplitItem() {
        return getItems().stream().map(OrderItem.class::cast).collect(Collectors.toList());
    }

    public List<Charge> choices6SplitItem(
            final OrderItem itemToSplit,
            final String newItemDescription,
            final BigDecimal newItemNetAmount,
            final BigDecimal newItemVatAmount,
            final Tax newItemtax,
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            final org.estatio.dom.asset.Property newItemProperty,
            final Project newItemProject,
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod
    ){
        List<Charge> result = chargeRepository.allIncoming();
        for (OrderItem item : getItems()) {
            if (item.getCharge()!=null && result.contains(item.getCharge())) {
                result.remove(item.getCharge());
            }
        }
        return result;
    }

    public List<BudgetItem> choices9SplitItem(
            final OrderItem itemToSplit,
            final String newItemDescription,
            final BigDecimal newItemNetAmount,
            final BigDecimal newItemVatAmount,
            final Tax newItemtax,
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            final org.estatio.dom.asset.Property newItemProperty,
            final Project newItemProject,
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod) {

        return budgetItemChooser.choicesBudgetItemFor(newItemProperty, newItemCharge);
    }

    public String validateSplitItem(
            final OrderItem itemToSplit,
            final String newItemDescription,
            final BigDecimal newItemNetAmount,
            final BigDecimal newItemVatAmount,
            final Tax newItemtax,
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            final org.estatio.dom.asset.Property newItemProperty,
            final Project newItemProject,
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod){
        return newItemPeriod == null ? null : PeriodUtil.reasonInvalidPeriod(newItemPeriod);
    }

    @Programmatic
    public <T> T ofFirstItem(final Function<OrderItem, T> f) {
        final Optional<OrderItem> firstItemIfAny = firstItemIfAny();
        return firstItemIfAny.map(f).orElse(null);
    }

    @Programmatic
    public Optional<OrderItem> firstItemIfAny() {
        return  getItems().stream()
                .findFirst();
    }

    @MemberOrder(name = "items", sequence = "3")
    public Order mergeItems(
            final OrderItem item,
            final OrderItem mergeInto){
        orderItemRepository.mergeItems(item, mergeInto);
        return this;
    }

    public String disableMergeItems() {
        if (isImmutable()) {
            return orderImmutableReason();
        }
        return getItems().size() < 2 ? "Merge needs 2 or more items" : null;
    }

    public boolean hideMergeItems(){
        return getItems().isEmpty();
    }

    public OrderItem default0MergeItems() {
        return firstItemIfAny()!=null ? getItems().last() : null;
    }

    public OrderItem default1MergeItems() {
        return firstItemIfAny()!=null ? getItems().first() : null;
    }

    public List<OrderItem> choices0MergeItems() {
        return getItems().stream().map(OrderItem.class::cast).collect(Collectors.toList());
    }

    public List<OrderItem> choices1MergeItems(
            final OrderItem item,
            final OrderItem mergeInto) {
        return getItems().stream().filter(x->!x.equals(item)).map(OrderItem.class::cast).collect(Collectors.toList());
    }

    public boolean isInvoiced(){
        return getNetAmountInvoiced().abs().compareTo(getNetAmount().abs()) >= 0;
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

    @Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getNetAmountInvoiced() {
        return orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedToOrder(this);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> amountExtractor) {
        return Lists.newArrayList(getItems()).stream()
                .map(amountExtractor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false"
    )
    @Property(hidden = Where.EVERYWHERE)
    @PropertyLayout(named = "Application Level Path")
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

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order changeOrderDetails(
            final String orderNumber,
            @ParameterLayout(named = "ECP (as buyer)")
            final Party ecpAsBuyer,
            final Party supplier,
            @Nullable
            final String sellerOrderReference,
            @Nullable
            final LocalDate orderDate
    ){
        setOrderNumber(orderNumber);
        setBuyer(ecpAsBuyer);
        setSeller(supplier);
        setSellerOrderReference(sellerOrderReference);
        setOrderDate(orderDate);
        return this;
    }

    public List<Party> autoComplete1ChangeOrderDetails(@MinLength(3) final String searchPhrase){
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public String validate1ChangeOrderDetails(final Party party){
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public List<Party> autoComplete2ChangeOrderDetails(@MinLength(3) final String searchPhrase){
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.SUPPLIER);
    }

    public String validate2ChangeOrderDetails(final Party party){
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.SUPPLIER);
    }

    public String default0ChangeOrderDetails(){
        return getOrderNumber();
    }

    public Party default1ChangeOrderDetails(){
        return getBuyer();
    }

    public Party default2ChangeOrderDetails(){
        return getSeller();
    }

    public String default3ChangeOrderDetails(){
        return getSellerOrderReference();
    }

    public LocalDate default4ChangeOrderDetails(){
        return getOrderDate();
    }

    public String disableChangeOrderDetails() {
        return reasonDisabledDueToState();
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

    private String orderImmutableReason(){
        return reasonDisabledDueToState();
    }


    @Programmatic
    public String reasonIncomplete(){

        String orderValidatorResult = new Validator()
                .checkNotNull(getOrderNumber(),"order number")
                .checkNotNull(getBuyer(), "buyer")
                .checkNotNull(getSeller(), "seller")
                .checkNotNull(getNetAmount(), "net amount")
                .checkNotNull(getGrossAmount(), "gross amount")
                .getResult();

        return mergeReasonItemsIncomplete(orderValidatorResult);

    }

    private String mergeReasonItemsIncomplete(final String validatorResult){
        if (reasonItemsIncomplete()!=null) {
            return validatorResult!=null ?
                    validatorResult.replace(" required", ", ").concat(reasonItemsIncomplete())
                    : reasonItemsIncomplete();
        } else {
            return validatorResult;
        }
    }

    static class Validator {

        public Validator() {
            this.result = null;
        }

        @Setter
        String result;

        String getResult() {
            return result != null ? result.concat(" required") : null;
        }

        Validator checkNotNull(Object mandatoryProperty, String propertyName) {
            if (mandatoryProperty == null) {
                setResult(result == null ? propertyName : result.concat(", ").concat(propertyName));
            }
            return this;
        }

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

    @Inject public
    OrderItemRepository orderItemRepository;

    @Inject
    BudgetItemChooser budgetItemChooser;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    OrganisationRepository organisationRepository;

    @Inject
    PartyRoleRepository partyRoleRepository;

    @Inject
    PartyRepository partyRepository;


}
