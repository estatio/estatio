package org.estatio.module.capex.dom.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.platform.applib.ReasonBuffer2;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.documents.BudgetItemChooser;
import org.estatio.module.capex.dom.items.FinancialItem;
import org.estatio.module.capex.dom.items.FinancialItemType;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.util.FinancialAmountUtil;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.tax.dom.Tax;

import lombok.Getter;
import lombok.Setter;
import static org.estatio.module.capex.dom.util.CountryUtil.isItalian;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "OrderItem"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE ordr.seller == :seller "),
        @Query(
                name = "findBySellerAndProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE ordr.seller == :seller &&  property == :property "),
        @Query(
                name = "findByOrderAndChargeAndItemNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE ordr == :ordr "
                        + "   && charge == :charge "
                        + "   && number == :number "),
        @Query(
                name = "findByProjectAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE project == :project "
                        + "   && charge == :charge "),
        @Query(
                name = "findByCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE charge == :charge "),
        @Query(
                name = "matchByDescription", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE description.matches(:description) "),
        @Query(
                name = "findByProject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE project == :project "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE property == :property "),
        @Query(
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.order.OrderItem "
                        + "WHERE budgetItem == :budgetItem ")
})

@Unique(name = "OrderItem_order_charge_number_UNQ", members = { "ordr", "charge", "number" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "orders.OrderItem"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class OrderItem extends UdoDomainObject2<OrderItem> implements FinancialItem {

    public String title() {
        final TitleBuilder titleBuilder = TitleBuilder.start()
                .withName(getDescription());
        if (getNetAmount() != null) {
            titleBuilder
                    .withName(" outstanding ")
                    .withName(netAmountOutstanding())
                    .withName(" of: ")
                    .withName(getNetAmount());
        }
        if (getCharge() != null) {
            titleBuilder
                    .withName(" ")
                    .withName(getCharge().getReference());
        }
        if (getOrdr().getSellerOrderReference() != null) {
            titleBuilder
                    .withName(" order: ")
                    .withName(getOrdr().getSellerOrderReference());
        } else {
            titleBuilder
                    .withName(" order: ")
                    .withName(getOrdr().getOrderNumber());
        }
        if (isOverspent()) {
            titleBuilder.withName(" (overspent)");
        }
        return titleBuilder.toString();
    }

    public String cssClass() {
        return isOverspent() ? "overspent" : null;
    }

    public OrderItem() {
        super("ordr,charge,number");
    }

    public OrderItem(
            final Order ordr,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem,
            final int number) {
        this();
        this.ordr = ordr;
        this.charge = charge;
        this.description = description;
        this.netAmount = netAmount;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
        this.tax = tax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.property = property;
        this.project = project;
        this.budgetItem = budgetItem;
        this.number = number;
    }

    public boolean isOverspent() {
        final BigDecimal netAmount = coalesce(getNetAmount(), BigDecimal.ZERO);

        final BigDecimal netAmountInvoiced =
                orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedToOrderItem(this);

        return netAmountInvoiced.compareTo(netAmount) > 0;
    }

    private static BigDecimal coalesce(final BigDecimal amount, final BigDecimal other) {
        return amount != null ? amount : other;
    }

    /**
     * Renamed from 'order' to avoid reserve keyword issues.
     */
    @Column(allowsNull = "false", name = "orderId")
    @Getter @Setter
    @PropertyLayout(named = "order", hidden = Where.REFERENCES_PARENT)
    private Order ordr;

    @Column(allowsNull = "true", name = "chargeId")
    @Getter @Setter
    private Charge charge;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public OrderItem editCharge(@Nullable final Charge charge) {
        setCharge(charge);
        getOrdr().updateOrderNumber();
        return this;
    }

    public Charge default0EditCharge() {
        return getCharge();
    }

    public List<Charge> autoComplete0EditCharge(@MinLength(3) String search) {
        return meService.me().getAtPath().startsWith("/ITA") ?
                chargeRepository.choicesItalianWorkTypes() :
                chargeRepository.findByApplicabilityAndMatchOnReferenceOrName(search, Applicability.INCOMING)
                        .stream()
                        .filter(x -> chargeNotUsedOnOrder(x))
                        .collect(Collectors.toList());
    }

    public String disableEditCharge() {
        return itemImmutableReasonIfIsImmutable();
    }

    @Programmatic
    boolean chargeNotUsedOnOrder(final Charge charge) {
        for (OrderItem item : this.getOrdr().getItems()) {
            if (item.getCharge() == charge) {
                return false;
            }
        }
        return true;
    }

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String description;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public OrderItem editDescription(
            @ParameterLayout(multiLine = InvoiceItem.DescriptionType.Meta.MULTI_LINE) final String description) {
        setDescription(description);
        return this;
    }

    public String default0EditDescription() {
        return getDescription();
    }

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal netAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal vatAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal grossAmount;

    @Column(allowsNull = "true", name = "taxId")
    @Getter @Setter
    private Tax tax;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public OrderItem updateAmounts(
            @Digits(integer = 13, fraction = 2) final BigDecimal netAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2) final BigDecimal vatAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2) final BigDecimal grossAmount,
            @Nullable final Tax tax) {
        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setGrossAmount(grossAmount);
        setTax(tax);
        return this;
    }

    public BigDecimal default0UpdateAmounts() {
        return getNetAmount();
    }

    public BigDecimal default1UpdateAmounts() {
        return getVatAmount();
    }

    public BigDecimal default2UpdateAmounts() {
        return getGrossAmount();
    }

    public Tax default3UpdateAmounts() {
        return getTax();
    }

    public String disableUpdateAmounts() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();
        itemImmutableIfOrderImmutable(buf);
        return buf.getReason();
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate endDate;

    public OrderItem editPeriod(@Nullable final String period) {
        if (PeriodUtil.isValidPeriod(period)) {
            setStartDate(PeriodUtil.yearFromPeriod(period).startDate());
            setEndDate(PeriodUtil.yearFromPeriod(period).endDate());
        }
        return this;
    }

    public boolean hideEditPeriod() {
        return meService.me().getAtPath().startsWith("/ITA");
    }

    public String default0EditPeriod() {
        return PeriodUtil.periodFromInterval(new LocalDateInterval(getStartDate(), getEndDate()));
    }

    public String validateEditPeriod(final String period) {
        return PeriodUtil.isValidPeriod(period) ? null : "Not a valid period";
    }

    public String disableEditPeriod() {
        return itemImmutableReasonIfIsImmutable();
    }

    @Column(allowsNull = "true", name = "propertyId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Property property;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public OrderItem editProperty(
            @Nullable final Property property) {
        setProperty(property);
        return this;
    }

    public Property default0EditProperty() {
        return (Property) getFixedAsset();
    }

    public String disableEditProperty() {
        return itemImmutableReasonIfIsImmutable();
    }

    @Column(allowsNull = "true", name = "projectId")
    @Getter @Setter
    private Project project;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public OrderItem editProject(
            @Nullable final Project project) {
        setProject(project);
        getOrdr().updateOrderNumber();
        return this;
    }

    public Project default0EditProject() {
        return getProject();
    }

    public List<Project> choices0EditProject() {
        return getFixedAsset() != null ?
                projectRepository.findByFixedAsset(getFixedAsset())
                        .stream()
                        .filter(x -> !x.isParentProject())
                        .filter(x -> x.getEndDate() == null || !x.getEndDate().isBefore(getEndDate() != null ? getEndDate() : LocalDate.now()))
                        .collect(Collectors.toList())
                : projectRepository.findWithoutFixedAsset();
    }

    public String disableEditProject() {
        return itemImmutableReasonIfIsImmutable();
    }

    public String validateEditProject(final Project project) {
        if (project != null && project.isParentProject())
            return "Parent project is not allowed";
        return null;
    }

    @Getter @Setter
    @Column(allowsNull = "true", name = "budgetItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetItem budgetItem;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public OrderItem editBudgetItem(
            @Nullable final BudgetItem budgetItem) {
        setBudgetItem(budgetItem);
        if (budgetItem != null)
            setCharge(budgetItem.getCharge());
        if (budgetItem != null)
            setProperty(budgetItem.getBudget().getProperty());
        return this;
    }

    public BudgetItem default0EditBudgetItem() {
        return getBudgetItem();
    }

    public List<BudgetItem> choices0EditBudgetItem() {
        return budgetItemChooser.choicesBudgetItemFor(getProperty(), getCharge());
    }

    public String disableEditBudgetItem() {
        return itemImmutableReasonIfIsImmutable();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private int number;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify.",
            hidden = Where.EVERYWHERE
    )
    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOrdr().getApplicationTenancy();
    }

    //region > FinancialItem impl'n (not otherwise implemented by the entity's properties)
    @Override
    @Programmatic
    public BigDecimal value() {
        return getNetAmount();
    }

    @Override
    public FinancialItemType getType() {
        return FinancialItemType.ORDERED;
    }

    @Override
    public FixedAsset<?> getFixedAsset() {
        return getProperty();
    }
    //endregion

    @Programmatic
    public String getPeriod() {
        return PeriodUtil.periodFromInterval(new LocalDateInterval(getStartDate(), getEndDate(), AbstractInterval.IntervalEnding.INCLUDING_END_DATE));
    }

    @Programmatic
    public boolean isInvoiced() {
        if (getNetAmount() == null) {
            return false;
        }
        return netAmountInvoiced().abs().compareTo(getNetAmount().abs()) >= 0;

    }

    @Programmatic
    public BigDecimal netAmountOutstanding() {
        return getNetAmount() != null ? getNetAmount().subtract(netAmountInvoiced()) : BigDecimal.ZERO;
    }

    @Programmatic
    BigDecimal netAmountInvoiced() {
        return orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedToOrderItem(this);
    }

    @Programmatic
    private boolean isImmutable() {
        return getOrdr().isImmutable() || isLinkedToInvoiceItem();
    }

    @Programmatic
    public boolean isLinkedToInvoiceItem() {
        return !orderItemInvoiceItemLinkRepository.findByOrderItem(this).isEmpty();
    }

    @Programmatic
    public String reasonIncomplete() {
        return new Validator()
                .checkNotNull(getDescription(), "description")
                .checkNotNull(getCharge(), "charge")
                .checkNotNull(getStartDate(), "start date")
                .checkNotNull(getEndDate(), "end date")
                .checkNotNull(getNetAmount(), "net amount")
                .checkGrossAmountForNonItaOnly(this)
                .validateConsistentDimensions(this)
                .getResult();
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

        Validator validateConsistentDimensions(OrderItem orderItem) {
            String message;
            if (orderItem.getProject() != null && orderItem.getBudgetItem() != null) {
                message = "either project or budget item - not both";
                setResult(result == null ? message : result.concat(", ").concat(message));
            }
            if (!isItalian(orderItem.getOrdr()) && orderItem.getProject() != null && orderItem.getProperty() == null) {
                message = "when project filled in then property";
                setResult(result == null ? message : result.concat(", ").concat(message));
            }
            if (orderItem.getBudgetItem() != null && orderItem.getProperty() == null) {
                message = "when budget item filled in then property";
                setResult(result == null ? message : result.concat(", ").concat(message));
            }
            return this;
        }

        Validator checkGrossAmountForNonItaOnly(OrderItem orderItem) {
            if (!isItalian(orderItem.getOrdr())) {
                if (orderItem.getGrossAmount() == null) {
                    setResult(result == null ? "gross amount" : result.concat(", ").concat("gross amount"));
                }
            }
            return this;
        }

    }

    @Programmatic
    public void subtractAmounts(final BigDecimal netAmountToSubtract, final BigDecimal vatAmountToSubtract, final BigDecimal grossAmountToSubtract) {
        setNetAmount(FinancialAmountUtil.subtractHandlingNulls(getNetAmount(), netAmountToSubtract));
        setVatAmount(FinancialAmountUtil.subtractHandlingNulls(getVatAmount(), vatAmountToSubtract));
        setGrossAmount(FinancialAmountUtil.subtractHandlingNulls(getGrossAmount(), grossAmountToSubtract));
    }

    @Programmatic
    public void addAmounts(final BigDecimal netAmountToAdd, final BigDecimal vatAmountToAdd, final BigDecimal grossAmountToAdd) {
        setNetAmount(FinancialAmountUtil.addHandlingNulls(getNetAmount(), netAmountToAdd));
        setVatAmount(FinancialAmountUtil.addHandlingNulls(getVatAmount(), vatAmountToAdd));
        setGrossAmount(FinancialAmountUtil.addHandlingNulls(getGrossAmount(), grossAmountToAdd));
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Order removeItem() {
        Order order = getOrdr();
        repositoryService.removeAndFlush(this);
        return order;
    }

    public String disableRemoveItem() {
        return itemImmutableReasonIfIsImmutable();
    }

    private String itemImmutableReasonIfIsImmutable() {
        final ReasonBuffer2 buf = ReasonBuffer2.forSingle();
        itemImmutableIfLinkedToInvoiceItem(buf);
        itemImmutableIfOrderImmutable(buf);
        return buf.getReason();
    }

    private void itemImmutableIfLinkedToInvoiceItem(final ReasonBuffer2 buf) {
        buf.append(this::isLinkedToInvoiceItem, "This order item is linked to an invoice item");
    }

    private void itemImmutableIfOrderImmutable(final ReasonBuffer2 buf) {
        buf.append(getOrdr()::isImmutable, "The order cannot be changed");
    }

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    private BudgetItemChooser budgetItemChooser;

    @Inject
    private MeService meService;

}
