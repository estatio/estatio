package org.estatio.module.capex.dom.order;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
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
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.documents.BudgetItemChooser;
import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.capex.dom.state.Stateful;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.dom.utils.IBANValidator;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.tax.dom.Tax;

import lombok.Getter;
import lombok.Setter;
import static org.estatio.module.capex.dom.util.CountryUtil.isItalian;

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
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE orderNumber == :orderNumber "),
        @Query(
                name = "findByBuyerAndBuyerOrderNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE buyer == :buyer "
                        + "   && buyerOrderNumber == :buyerOrderNumber "),
        @Query(
                name = "findByBuyerAndExtRefOrderGlobalNumerator", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE buyer == :buyer "
                        + "   && orderNumber.startsWith(:extRefOrderGlobalNumeratorWithTrailingSlash) "),
        @Query(
                name = "matchByOrderNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE orderNumber.matches(:orderNumber) "),
        @Query(
                name = "findBySellerOrderReferenceAndSeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE sellerOrderReference == :sellerOrderReference "
                        + "   && seller == :seller "),
        @Query(
                name = "findBySellerOrderReferenceAndSellerAndOrderDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE sellerOrderReference == :sellerOrderReference "
                        + "   && seller == :seller "
                        + "   && orderDate == :orderDate "),
        @Query(
                name = "findByOrderDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE orderDate >= :fromDate "
                        + "   && orderDate <= :toDate "),
        @Query(
                name = "findByEntryDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE entryDate >= :fromDate "
                        + "   && entryDate <= :toDate "),
        @Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE seller == :seller "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE property == :property "
                        + "ORDER BY entryDate"),
        @Query(
                name = "findByPropertyAndSeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.Order "
                        + "WHERE property == :property "
                        + "&& seller == :seller "
                        + "ORDER BY entryDate")
})
@Indices({
        @Index(name = "Order_sellerOrderReference_IDX", members = { "sellerOrderReference" }),
        @Index(name = "Order_buyer_buyerOrderNumber_IDX", members = { "buyer", "buyerOrderNumber" })
})
@Unique(name = "Order_reference_UNQ", members = { "orderNumber" })
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
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent<Order> {

    }

    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent<Order> {

    }

    public static class ObjectRemovingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectRemovingEvent<Order> {

    }

    public Order() {
        // TODO: may need to revise this when we know more...
        super("seller, orderDate, orderNumber, id");
    }

    public Order(
            final org.estatio.module.asset.dom.Property property,
            final IncomingInvoiceType orderType,
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
        this.type = orderType;
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

        buf.append(getBarcode());

        final Party seller = getSeller();
        if (seller != null) {
            buf.append(": ", seller);
        }

        final String orderNumber = getOrderNumber();
        if (orderNumber != null) {
            buf.append(", ", orderNumber);
        }

        return buf.toString();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public Order completeOrder(
            final IncomingInvoiceType orderType,
            final org.estatio.module.asset.dom.Property property,
            final String orderNumber,
            final Party supplier,
            final @Nullable Boolean createRoleIfRequired,
            final @ParameterLayout(named = "Supplier order ref.") String supplierOrderReference,
            final LocalDate orderDate) {
        setType(orderType);
        setOrderNumber(orderNumber);
        setSellerOrderReference(supplierOrderReference);
        setOrderDate(orderDate);
        setSeller(supplier);
        setProperty(property);

        return this;
    }

    public IncomingInvoiceType default0CompleteOrder() {
        return getType();
    }

    public org.estatio.module.asset.dom.Property default1CompleteOrder() {
        return getProperty();
    }

    public List<org.estatio.module.asset.dom.Property> choices1CompleteOrder() {
        List<org.estatio.module.asset.dom.Property> result = new ArrayList<>();
        if (getBuyer() != null) {
            for (FixedAssetRole role : fixedAssetRoleRepository.findByPartyAndType(getBuyer(), FixedAssetRoleTypeEnum.PROPERTY_OWNER)) {
                if (role.getAsset().getClass().isAssignableFrom(org.estatio.module.asset.dom.Property.class)) {
                    result.add((org.estatio.module.asset.dom.Property) role.getAsset());
                }
            }
        }
        return result.size() > 0 ? result : propertyRepository.allProperties();
    }

    public String default2CompleteOrder() {
        return getOrderNumber();
    }

    public Party default3CompleteOrder() {
        return getSeller();
    }

    public List<Party> autoComplete3CompleteOrder(final @MinLength(3) String search) {
        return partyRepository.autoCompleteSupplier(search, getAtPath());
    }

    public String default5CompleteOrder() {
        return getSellerOrderReference();
    }

    public LocalDate default6CompleteOrder() {
        return getOrderDate();
    }

    public String validateCompleteOrder(
            final IncomingInvoiceType orderType,
            final org.estatio.module.asset.dom.Property property,
            final String orderNumber,
            final Party supplier,
            final Boolean createRoleIfRequired,
            final String supplierOrderReference,
            final LocalDate orderDate) {
        // validate seller
        final String sellerValidation = partyRoleRepository.validateThat(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        if ((createRoleIfRequired == null || !createRoleIfRequired) && sellerValidation != null) {
            return sellerValidation;
        }

        return null;
    }

    public String disableCompleteOrder() {
        return reasonDisabledDueToState();
    }

    public boolean hideCompleteOrder() {
        return getAtPath().startsWith("/ITA");
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public Order completeOrderItem(
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project,
            final BudgetItem budgetItem,
            final String period) {
        Optional<OrderItem> firstItemIfAny = getItems().stream().findFirst();
        if (firstItemIfAny.isPresent()) {
            OrderItem orderItem = firstItemIfAny.get();
            orderItem.setCharge(charge);
            orderItem.setDescription(description);
            orderItem.setNetAmount(netAmount);
            orderItem.setVatAmount(vatAmount);
            orderItem.setGrossAmount(grossAmount);
            orderItem.setTax(tax);
            orderItem.setStartDate(PeriodUtil.startDateFromPeriod(period));
            orderItem.setEndDate(PeriodUtil.endDateFromPeriod(period));
            orderItem.setProperty(getProperty());
            orderItem.setProject(project);
            orderItem.setBudgetItem(budgetItem);
        } else {
            addItem(
                    charge,
                    description,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    tax,
                    period,
                    getProperty(),
                    project,
                    budgetItem
            );
        }

        return this;
    }

    public String default0CompleteOrderItem() {
        return getDescriptionSummary();
    }

    public BigDecimal default1CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getNetAmount).orElse(null);
    }

    public BigDecimal default2CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getVatAmount).orElse(null);
    }

    public Tax default3CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getTax).orElse(null);
    }

    public BigDecimal default4CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getGrossAmount).orElse(null);
    }

    public Charge default5CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getCharge).orElse(null);
    }

    public List<Charge> autoComplete5CompleteOrderItem(@MinLength(3) String search) {
        return chargeRepository.findByApplicabilityAndMatchOnReferenceOrName(search, Applicability.INCOMING);
    }

    public Project default6CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getProject).orElse(null);
    }

    public List<Project> choices6CompleteOrderItem() {
        return getProperty() == null ?
                projectRepository.listAll()
                : projectRepository.findByFixedAsset(getProperty())
                .stream()
                .filter(x -> !x.isParentProject())
                .filter(x -> {
                    LocalDate endDateFromPeriod = PeriodUtil.endDateFromPeriod(default8CompleteOrderItem());
                    LocalDate dateToUse = endDateFromPeriod != null ? endDateFromPeriod : clockService.now();
                    return x.getEndDate() == null || !x.getEndDate().isBefore(dateToUse);
                })
                .collect(Collectors.toList());
    }

    public BudgetItem default7CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getBudgetItem).orElse(null);
    }

    public List<BudgetItem> choices7CompleteOrderItem(
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project) {
        return budgetItemChooser.choicesBudgetItemFor(getProperty(), charge);
    }

    public String default8CompleteOrderItem() {
        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        return firstItemIfAny.map(OrderItem::getPeriod).orElse(null);
    }

    public String disableCompleteOrderItem() {
        return reasonDisabledDueToState();
    }

    public String validateCompleteOrderItem(
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project,
            final BudgetItem budgetItem,
            final String period) {
        if (project != null && project.isParentProject())
            return "Parent project is not allowed";

        if (project != null && getType() != IncomingInvoiceType.CAPEX)
            return "Project can only be added to orders of type CAPEX";

        return period != null ? PeriodUtil.reasonInvalidPeriod(period) : null;
    }

    public boolean hideCompleteOrderItem() {
        return getAtPath().startsWith("/ITA");
    }

    private Optional<OrderItem> getFirstItemIfAny() {
        return getItems().stream().findFirst();
    }

    /**
     * This relates to the owning property, while the child items may either also relate to the property,
     * or could potentially relate to individual units within the property.
     *
     * <p>
     * This follows the same pattern as {@link IncomingInvoice}.
     * </p>
     */
    @javax.jdo.annotations.Column(name = "propertyId", allowsNull = "true")
    @Getter @Setter
    private org.estatio.module.asset.dom.Property property;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editProperty(
            @Nullable final org.estatio.module.asset.dom.Property property,
            final boolean changeOnItemsAsWell) {
        setProperty(property);
        if (changeOnItemsAsWell) {
            com.google.common.collect.Lists.newArrayList(getItems())  // eagerly load (DN 4.x collections do not support streaming)
                    .stream()
                    .map(OrderItem.class::cast)
                    .forEach(x -> x.setProperty(property));
        }
        updateOrderNumber();

        return this;
    }

    public org.estatio.module.asset.dom.Property default0EditProperty() {
        return getProperty();
    }

    public boolean default1EditProperty() {
        return true;
    }

    public String disableEditProperty() {
        if (isImmutable()) {
            return orderImmutableReason();
        }
        return propertyIsImmutableReason();
    }

    private String propertyIsImmutableReason() {
        for (OrderItem item : getItems()) {
            if (item.isLinkedToInvoiceItem()) {
                return "Property cannot be changed because an item is linked to an invoice";
            }
        }
        return null;
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private IncomingInvoiceType type;

    public boolean hideType() {
        return meService.me().getAtPath().startsWith("/ITA");
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editType(
            final IncomingInvoiceType type) {
        setType(type);
        return this;
    }

    public String disableEditType() {
        if (isImmutable()) {
            return orderImmutableReason();
        }
        return typeIsImmutableReason();
    }

    public IncomingInvoiceType default0EditType() {
        return getType();
    }

    private String typeIsImmutableReason() {
        for (OrderItem item : getItems()) {
            if (item.isLinkedToInvoiceItem()) {
                return "Type cannot be changed because an item is linked to an invoice";
            }
        }
        return null;
    }

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String orderNumber;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editOrderNumber(
            @Nullable final String orderNumber) {
        setOrderNumber(orderNumber);
        return this;
    }

    public String default0EditOrderNumber() {
        return getOrderNumber();
    }

    public String disableEditOrderNumber() {
        if (getAtPath().startsWith("/ITA"))
            return "Order number is generated automatically for Italian order"; // May be redundant since is hidden for italian user; but just to be explicit for other users ...
        return orderImmutableReason();
    }

    public boolean hideEditOrderNumber() {
        return meService.me().getAtPath().startsWith("/ITA");
    }

    public void updateOrderNumber() {
        if (getAtPath().startsWith("/ITA")) {
            String possibleMultiPropertyReference = orderNumber.split("/").length > 1 ? orderNumber.split("/")[1] : "";
            String orderNumber = orderRepository.toItaOrderNumber(getBuyerOrderNumber().toString(), getProperty(), possibleMultiPropertyReference, getItems().first().getProject(), getItems().first().getCharge());
            setOrderNumber(orderNumber);
        }
    }

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    @PropertyLayout(named = "Supplier order ref.")
    private String sellerOrderReference;

    public boolean hideSellerOrderReference() {
        return meService.me().getAtPath().startsWith("/ITA");
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order editSellerOrderReference(
            @Nullable final String sellerOrderReference) {
        setSellerOrderReference(sellerOrderReference);
        return this;
    }

    public String default0EditSellerOrderReference() {
        return getSellerOrderReference();
    }

    public String disableEditSellerOrderReference() {
        return orderImmutableReason();
    }

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate entryDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate orderDate;

    public Order changeDates(
            @Nullable final LocalDate orderDate,
            @Nullable final LocalDate entryDate
    ) {
        setOrderDate(orderDate);
        setEntryDate(entryDate);
        return this;
    }

    public LocalDate default0ChangeDates() {
        return getOrderDate();
    }

    public LocalDate default1ChangeDates() {
        return getEntryDate();
    }

    public String disableChangeDates() {
        return orderImmutableReason();
    }

    @Column(allowsNull = "true", name = "sellerPartyId")
    @PropertyLayout(named = "Supplier")
    @Getter @Setter
    private Party seller;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit Supplier")
    public Order editSeller(
            @Nullable final Party supplier,
            final boolean createRoleIfRequired) {
        setSeller(supplier);
        if (supplier != null && createRoleIfRequired) {
            partyRoleRepository.findOrCreate(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return this;
    }

    public String validateEditSeller(
            final Party supplier,
            final boolean createRoleIfRequired) {
        if (supplier != null && !createRoleIfRequired) {
            // requires that the supplier already has this role
            return partyRoleRepository.validateThat(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return null;
    }

    public List<Party> autoComplete0EditSeller(final String search) {
        return partyRepository.autoCompleteSupplier(search, getAtPath());
    }

    public Party default0EditSeller() {
        return getSeller();
    }

    public String disableEditSeller() {
        if (isImmutable()) {
            return orderImmutableReason();
        }
        return sellerIsImmutableReason();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(named = "Create Supplier")
    public Order createSeller(
            final OrganisationNameNumberViewModel candidate,
            final Country country,
            @Nullable final String ibanNumber) {
        Organisation organisation = organisationRepository
                .newOrganisation(null, true, candidate.getOrganisationName(), country);
        if (candidate.getChamberOfCommerceCode() != null)
            organisation.setChamberOfCommerceCode(candidate.getChamberOfCommerceCode());
        setSeller(organisation);
        if (ibanNumber != null) {
            bankAccountRepository.newBankAccount(organisation, ibanNumber, null);
        }
        partyRoleRepository.findOrCreate(organisation, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        return this;
    }

    public List<OrganisationNameNumberViewModel> autoComplete0CreateSeller(@MinLength(3) final String search) {
        String atPath = getAtPath();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // nothing
        }
        List<OrganisationNameNumberViewModel> result = new ArrayList<>();
        result.addAll(chamberOfCommerceCodeLookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(search, atPath));
        result.add(new OrganisationNameNumberViewModel(search, null));
        return result;
    }

    public String validateCreateSeller(
            final OrganisationNameNumberViewModel candidate,
            final Country country,
            final String ibanNumber) {
        if (ibanNumber != null && !IBANValidator.valid(ibanNumber)) {
            return String.format("%s is not a valid iban number", ibanNumber);
        }
        return null;
    }

    public boolean hideCreateSeller() {
        return meService.me().getAtPath().startsWith("/ITA");
    }

    public String disableCreateSeller() {
        if (isImmutable()) {
            return orderImmutableReason();
        }
        return sellerIsImmutableReason();
    }

    private String sellerIsImmutableReason() {
        for (OrderItem item : getItems()) {
            if (item.isLinkedToInvoiceItem()) {
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
            @Nullable final Party buyer) {
        setBuyer(buyer);
        return this;
    }

    public List<Party> autoComplete0EditBuyer(@MinLength(3) final String searchPhrase) {
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public String validate0EditBuyer(final Party party) {
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public Party default0EditBuyer() {
        return getBuyer();
    }

    public String disableEditBuyer() {
        return orderImmutableReason();
    }

    @Column(allowsNull = "true", scale = 0, length = 8)
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private BigInteger buyerOrderNumber;

    @Persistent(mappedBy = "ordr", dependentElement = "true")
    @Getter @Setter
    private SortedSet<OrderItem> items = new TreeSet<>();

    @MemberOrder(name = "items", sequence = "1")
    public Order addItem(
            final Charge charge,
            @Nullable final String description,
            @Digits(integer = 13, fraction = 2) final BigDecimal netAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2) final BigDecimal vatAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2) final BigDecimal grossAmount,
            @Nullable final Tax tax,
            @Nullable final String period,
            @Nullable final org.estatio.module.asset.dom.Property property,
            @Nullable final Project project,
            @Nullable final BudgetItem budgetItem
    ) {
        orderItemRepository.upsert(
                this, charge, description, netAmount, vatAmount, grossAmount, tax, PeriodUtil.yearFromPeriod(period).startDate(), PeriodUtil.yearFromPeriod(period).endDate(), property, project, budgetItem, determineItemNumber(charge));

        return this;
    }

    private int determineItemNumber(final Charge chargeNewItem) {
        if (getAtPath().startsWith("/ITA")) {
            List<OrderItem> itemsWithSameCharge = Lists.newArrayList(getItems()).stream()
                    .filter(x -> x.getCharge() != null)
                    .filter(x -> x.getCharge().equals(chargeNewItem))
                    .collect(Collectors.toList());
            return itemsWithSameCharge.size();
        } else {
            return 0;
        }
    }

    public Charge default0AddItem() {
        if (meService.me().getAtPath().startsWith("/ITA")) {
            return getItems().isEmpty() ? null : getItems().first().getCharge();
        }
        return null;
    }

    public List<Charge> choices0AddItem() {
        if (meService.me().getAtPath().startsWith("/ITA")) {
            return chargeRepository.choicesItalianWorkTypes();
        }

        List<Charge> result = chargeRepository.allIncoming();
        for (OrderItem item : getItems()) {
            if (item.getCharge() != null && result.contains(item.getCharge())) {
                result.remove(item.getCharge());
            }
        }
        return result;
    }

    public String default6AddItem() {
        return ofFirstItem(OrderItem::getStartDate) != null ? PeriodUtil.periodFromInterval(new LocalDateInterval(ofFirstItem(OrderItem::getStartDate), ofFirstItem(OrderItem::getEndDate))) : null;
    }

    public org.estatio.module.asset.dom.Property default7AddItem() {
        return ofFirstItem(OrderItem::getProperty);
    }

    public Project default8AddItem() {
        return ofFirstItem(OrderItem::getProject);
    }

    public String validateAddItem(
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final String period,
            final org.estatio.module.asset.dom.Property property,
            final Project project,
            final BudgetItem budgetItem) {
        if (period != null && !period.equals("")) {
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
            final org.estatio.module.asset.dom.Property property,
            final Project project,
            final BudgetItem budgetItem) {

        return budgetItemChooser.choicesBudgetItemFor(property, charge);
    }

    public String disableAddItem() {
        return orderImmutableReason();
    }

    @MemberOrder(name = "items", sequence = "2")
    public Order splitItem(
            final OrderItem itemToSplit,
            final String newItemDescription,
            @Digits(integer = 13, fraction = 2) final BigDecimal newItemNetAmount,
            @Nullable @Digits(integer = 13, fraction = 2) final BigDecimal newItemVatAmount,
            @Nullable final Tax newItemtax,
            @Digits(integer = 13, fraction = 2) final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            @Nullable final org.estatio.module.asset.dom.Property newItemProperty,
            @Nullable final Project newItemProject,
            @Nullable final BudgetItem newItemBudgetItem,
            @Nullable final String newItemPeriod
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
                newItemBudgetItem,
                0);
        return this;
    }

    public boolean hideSplitItem() {
        return getItems().isEmpty() || meService.me().getAtPath().startsWith("/ITA");
    }

    public OrderItem default0SplitItem() {
        return firstItemIfAny() != null ? getItems().first() : null;
    }

    public Tax default4SplitItem() {
        return ofFirstItem(OrderItem::getTax);
    }

    public org.estatio.module.asset.dom.Property default7SplitItem() {
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

    public List<Charge> choices6SplitItem() {
        List<Charge> chargesOnItems = getItems().stream()
                .map(OrderItem::getCharge)
                .collect(Collectors.toList());

        return getType() == IncomingInvoiceType.CAPEX ?
                chargeRepository.allIncoming().stream()
                        .filter(charge -> !chargesOnItems.contains(charge))
                        .collect(Collectors.toList()) :
                chargeRepository.allIncoming();
    }

    public List<BudgetItem> choices9SplitItem(
            final OrderItem itemToSplit,
            final String newItemDescription,
            final BigDecimal newItemNetAmount,
            final BigDecimal newItemVatAmount,
            final Tax newItemtax,
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            final org.estatio.module.asset.dom.Property newItemProperty,
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
            final org.estatio.module.asset.dom.Property newItemProperty,
            final Project newItemProject,
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod) {
        return newItemPeriod == null ? null : PeriodUtil.reasonInvalidPeriod(newItemPeriod);
    }

    @Programmatic
    public <T> T ofFirstItem(final Function<OrderItem, T> f) {
        final Optional<OrderItem> firstItemIfAny = firstItemIfAny();
        return firstItemIfAny.map(f).orElse(null);
    }

    @Programmatic
    public Optional<OrderItem> firstItemIfAny() {
        return getItems().stream()
                .findFirst();
    }

    @MemberOrder(name = "items", sequence = "3")
    public Order mergeItems(
            final OrderItem item,
            final OrderItem mergeInto) {
        orderItemRepository.mergeItems(item, mergeInto);
        return this;
    }

    public String disableMergeItems() {
        if (isImmutable()) {
            return orderImmutableReason();
        }
        return getItems().size() < 2 ? "Merge needs 2 or more items" : null;
    }

    public boolean hideMergeItems() {
        return getItems().isEmpty() || meService.me().getAtPath().startsWith("/ITA");
    }

    public OrderItem default0MergeItems() {
        return firstItemIfAny() != null ? getItems().last() : null;
    }

    public OrderItem default1MergeItems() {
        return firstItemIfAny() != null ? getItems().first() : null;
    }

    public List<OrderItem> choices0MergeItems() {
        return getItems().stream().map(OrderItem.class::cast).collect(Collectors.toList());
    }

    public List<OrderItem> choices1MergeItems(
            final OrderItem item,
            final OrderItem mergeInto) {
        return getItems().stream().filter(x -> !x.equals(item)).map(OrderItem.class::cast).collect(Collectors.toList());
    }

    public boolean isInvoiced() {
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
        if (stateTransitionClass == OrderApprovalStateTransition.class) {
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
        if (stateTransitionClass == OrderApprovalStateTransition.class) {
            setApprovalState((OrderApprovalState) newState);
        }
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Order changeOrderDetails(
            final String orderNumber,
            @ParameterLayout(named = "ECP (as buyer)") final Party ecpAsBuyer,
            final Party supplier,
            @Nullable final String sellerOrderReference,
            @Nullable final LocalDate orderDate
    ) {
        setOrderNumber(orderNumber);
        setBuyer(ecpAsBuyer);
        setSeller(supplier);
        setSellerOrderReference(sellerOrderReference);
        setOrderDate(orderDate);
        return this;
    }

    public List<Party> autoComplete1ChangeOrderDetails(@MinLength(3) final String searchPhrase) {
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public String validate1ChangeOrderDetails(final Party party) {
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public List<Party> autoComplete2ChangeOrderDetails(@MinLength(3) final String searchPhrase) {
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.SUPPLIER);
    }

    public String validate2ChangeOrderDetails(final Party party) {
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.SUPPLIER);
    }

    public String default0ChangeOrderDetails() {
        return getOrderNumber();
    }

    public Party default1ChangeOrderDetails() {
        return getBuyer();
    }

    public Party default2ChangeOrderDetails() {
        return getSeller();
    }

    public String default3ChangeOrderDetails() {
        return getSellerOrderReference();
    }

    public LocalDate default4ChangeOrderDetails() {
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
    public boolean isImmutable() {
        return reasonDisabledDueToState() != null;
    }

    private String orderImmutableReason() {
        return reasonDisabledDueToState();
    }

    @Programmatic
    public String reasonIncomplete() {

        String orderValidatorResult = new Validator()
                .checkNotNull(getType(), "type")
                .checkNotNull(getOrderNumber(), "order number")
                .checkNotNull(getBuyer(), "buyer")
                .checkNotNull(getSeller(), "seller")
                .checkNotNull(getNetAmount(), "net amount")
                .checkNotNull(getGrossAmount(), "gross amount")
                .validateForOrderType(this)
                .getResult();

        return mergeReasonItemsIncomplete(orderValidatorResult);

    }

    private String mergeReasonItemsIncomplete(final String validatorResult) {
        if (reasonItemsIncomplete() != null) {
            return validatorResult != null ?
                    validatorResult.replace(" required", ", ").concat(reasonItemsIncomplete())
                    : reasonItemsIncomplete();
        } else {
            return validatorResult;
        }
    }

    @PropertyLayout(hidden = Where.OBJECT_FORMS)
    public String getBarcode() {
        final Optional<Document> document = lookupAttachedPdfService.lookupOrderPdfFrom(this);
        return document.map(DocumentAbstract::getName).orElse(null);
    }

    //region > notification

    @Property(editing = Editing.DISABLED)
    @PropertyLayout(multiLine = 5)
    public String getNotification() {
        final StringBuilder result = new StringBuilder();

        final String noBuyerBarcodeMatch = buyerBarcodeMatchValidation();
        if (noBuyerBarcodeMatch != null) {
            result.append(noBuyerBarcodeMatch);
        }

        final String sameOrderNumberCheck = doubleOrderCheck();
        if (sameOrderNumberCheck != null) {
            result.append(sameOrderNumberCheck);
        }

        return result.length() > 0 ? result.toString() : null;

    }

    public boolean hideNotification() {
        return isItalian(this) || getNotification() == null;
    }

    String doubleOrderCheck() {
        final String doubleOrderCheck = possibleDoubleOrder();
        if (doubleOrderCheck != null) {
            return doubleOrderCheck;
        }
        final String sameNumberCheck = sameSellerOrderReference();
        if (sameNumberCheck != null) {
            return sameNumberCheck;
        }
        return null;
    }

    private String possibleDoubleOrder() {
        if (getSellerOrderReference() == null || getSeller() == null || getOrderDate() == null) {
            return null;
        }

        Order possibleDouble = orderRepository.findBySellerOrderReferenceAndSellerAndOrderDate(getSellerOrderReference(), getSeller(), getOrderDate());
        if (possibleDouble == null || possibleDouble.equals(this)) {
            return null;
        }

        return "WARNING: There is already an order with the same seller order reference and order date for this seller. Please check.";
    }

    private String sameSellerOrderReference() {
        if (getSellerOrderReference() == null || getSeller() == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        orderRepository.findBySellerOrderReferenceAndSeller(getSellerOrderReference(), getSeller())
                .stream()
                .filter(order -> !order.equals(this))
                .forEach(order -> {
                    if (builder.indexOf("WARNING: Orders with the same seller order reference of this seller are found ") == -1)
                        builder.append("WARNING: Orders with the same seller order reference of this seller are found ");

                    if (order.getOrderDate() != null)
                        builder.append(String.format("on date %s; ", order.getOrderDate().toString()));
                });

        return builder.length() != 0 ? builder.toString() : null;
    }

    private String buyerBarcodeMatchValidation() {
        if (getBuyer() != null) {
            if (buyerFinder.buyerDerivedFromDocumentName(this) == null) {
                return null; // covers all cases where no buyer could be derived from document name
            }
            if (!getBuyer().equals(buyerFinder.buyerDerivedFromDocumentName(this))) {
                return "Buyer does not match barcode (document name); ";
            }
        }
        return null;
    }

    //endregion

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

        Order.Validator validateForOrderType(Order order) {
            if (order == null)
                return this;
            if (order.getType() == null)
                return this;

            String message;
            switch (order.getType()) {

                case CAPEX:
                case SERVICE_CHARGES:
                case PROPERTY_EXPENSES:
                    message = "property";
                    if (order.getProperty() == null) {
                        setResult(result == null ? message : result.concat(", ").concat(message));
                    }
                    break;

                default:
            }

            return this;
        }

    }

    @Programmatic
    public String reasonItemsIncomplete() {
        StringBuffer buffer = new StringBuffer();
        for (OrderItem item : getItems()) {
            if (item.reasonIncomplete() != null) {
                buffer.append("(on item) ");
                buffer.append(item.reasonIncomplete());
            }
        }
        return buffer.length() == 0 ? null : buffer.toString();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, hidden = Where.OBJECT_FORMS)
    public String getDescriptionSummary() {
        StringBuffer summary = new StringBuffer();
        boolean first = true;
        for (OrderItem orderItem : getItems()) {
            if (orderItem.getDescription() != null && orderItem.getDescription() != "") {
                if (!first) {
                    summary.append(" | ");
                }
                summary.append(orderItem.getDescription());
                first = false;
            }
        }
        return summary.toString();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Order attachPdf(
            final Blob pdf,
            final boolean replaceExistingPdf) {
        if (replaceExistingPdf)
            paperclipRepository.deleteIfAttachedTo(this, PaperclipRepository.Policy.PAPERCLIPS_AND_DOCUMENTS_IF_ORPHANED);

        return newDocument(pdf);
    }

    @Programmatic
    public Order newDocument(final Blob pdf) {
        Document currentDoc = lookupAttachedPdfService.lookupOrderPdfFrom(this).isPresent() ? lookupAttachedPdfService.lookupOrderPdfFrom(this).get() : null;
        DocumentType type = DocumentTypeData.INCOMING_ORDER.findUsing(documentTypeRepository);
        documentService.createAndAttachDocumentForBlob(type, this.getAtPath(), pdf.getName(), pdf, null, this);
        if (currentDoc != null)
            messageService.warnUser("Order now has multiple documents attached.");
        return this;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    public OrderItemRepository orderItemRepository;

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

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    ChamberOfCommerceCodeLookUpService chamberOfCommerceCodeLookUpService;

    @Inject
    DocumentService documentService;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    MessageService messageService;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject
    private MeService meService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ClockService clockService;

    @Inject
    BuyerFinder buyerFinder;

}
