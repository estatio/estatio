package org.estatio.capex.dom.order;

import java.math.BigDecimal;
import java.util.List;

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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Applicability;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.FinancialAmountUtil;

import lombok.Getter;
import lombok.Setter;

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
                name = "findByOrderAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE ordr == :ordr "
                        + "   && charge == :charge "),
        @Query(
                name = "findByProjectAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE project == :project "
                        + "   && charge == :charge "),
        @Query(
                name = "findByCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE charge == :charge "),
        @Query(
                name = "matchByDescription", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE description.matches(:description) "),
        @Query(
                name = "findByProject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE project == :project "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE property == :property "),
        @Query(
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE budgetItem == :budgetItem ")
})

@Unique(name = "OrderItem_order_charge_UNQ", members = { "ordr", "charge" })
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
        return TitleBuilder.start()
                .withName(getDescription().concat(" "))
                .withName(getNetAmount())
                .withName(" ")
                .withName(getOrdr().getOrderNumber())
                .toString();
    }

    public OrderItem() {
        super("ordr,charge");
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
            final BudgetItem budgetItem) {
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
    @ActionLayout(promptStyle = PromptStyle.INLINE_AS_IF_EDIT)
    public OrderItem editCharge(@Nullable final Charge charge) {
        setCharge(charge);
        return this;
    }

    public Charge default0EditCharge(){
        return getCharge();
    }

    public List<Charge> autoComplete0EditCharge(@MinLength(3) String search){
        return chargeRepository.findByApplicabilityAndMatchOnReferenceOrName(search, Applicability.INCOMING);
    }

    public String disableEditCharge(){
        return isImmutable() ? itemImmutableReason() : null;
    }

    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String description;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE_AS_IF_EDIT)
    public OrderItem editDescription(
            @ParameterLayout(multiLine = InvoiceItem.DescriptionType.Meta.MULTI_LINE)
            final String description) {
        setDescription(description);
        return this;
    }

    public String default0EditDescription(){
        return getDescription();
    }

    public String disableEditDescription(){
        return isImmutable() ? itemImmutableReason() : null;
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
            @Digits(integer=13, fraction = 2)
            final BigDecimal netAmount,
            @Nullable
            @Digits(integer=13, fraction = 2)
            final BigDecimal vatAmount,
            @Nullable
            @Digits(integer=13, fraction = 2)
            final BigDecimal grossAmount,
            @Nullable
            final Tax tax){
        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setGrossAmount(grossAmount);
        setTax(tax);
        return this;
    }

    public BigDecimal default0UpdateAmounts(){
        return getNetAmount();
    }

    public BigDecimal default1UpdateAmounts(){
        return getVatAmount();
    }

    public BigDecimal default2UpdateAmounts(){
        return getGrossAmount();
    }

    public Tax default3UpdateAmounts(){
        return getTax();
    }

    public String disableUpdateAmounts(){
        return isImmutable() ? itemImmutableReason() : null;
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate endDate;

    public OrderItem editPeriod(@Nullable final String period){
        if (PeriodUtil.isValidPeriod(period)){
            setStartDate(PeriodUtil.yearFromPeriod(period).startDate());
            setEndDate(PeriodUtil.yearFromPeriod(period).endDate());
        }
        return this;
    }

    public String default0EditPeriod(){
        return PeriodUtil.periodFromInterval(new LocalDateInterval(getStartDate(), getEndDate()));
    }

    public String validateEditPeriod(final String period){
        return PeriodUtil.isValidPeriod(period) ? null : "Not a valid period";
    }

    public String disableEditPeriod(){
        return isImmutable() ? itemImmutableReason() : null;
    }

    @Column(allowsNull = "true", name = "propertyId")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Property property;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE_AS_IF_EDIT)
    public OrderItem editProperty(
            @Nullable
            final org.estatio.dom.asset.Property property){
        setProperty(property);
        return this;
    }

    public org.estatio.dom.asset.Property default0EditProperty(){
        return (org.estatio.dom.asset.Property) getFixedAsset();
    }

    public String disableEditProperty(){
        return isImmutable() ? itemImmutableReason() : null;
    }

    @Column(allowsNull = "true", name = "projectId")
    @Getter @Setter
    private Project project;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE_AS_IF_EDIT)
    public OrderItem editProject(
            @Nullable
            final Project project){
        setProject(project);
        return this;
    }

    public Project default0EditProject(){
        return getProject();
    }

    public List<Project> choices0EditProject(){
        return getFixedAsset()!=null ? projectRepository.findByFixedAsset(getFixedAsset()) : null;
    }

    public String disableEditProject(){
        return isImmutable() ? itemImmutableReason() : null;
    }

    @Getter @Setter
    @Column(allowsNull = "true", name="budgetItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetItem budgetItem;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE_AS_IF_EDIT)
    public OrderItem editBudgetItem(
            @Nullable
            final BudgetItem budgetItem){
        setBudgetItem(budgetItem);
        setCharge(budgetItem.getCharge());
        setProperty(budgetItem.getBudget().getProperty());
        return this;
    }

    public BudgetItem default0EditBudgetItem(){
        return getBudgetItem();
    }

    public List<BudgetItem> choices0EditBudgetItem() {
        return budgetItemChooser.choicesBudgetItemFor(getProperty(), getCharge());
    }

    public String disableEditBudgetItem(){
        return isImmutable() ? itemImmutableReason() : null;
    }

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
    public String getPeriod(){
        return PeriodUtil.periodFromInterval(new LocalDateInterval(getStartDate(), getEndDate(), AbstractInterval.IntervalEnding.INCLUDING_END_DATE));
    }

    @Programmatic
    public boolean isInvoiced(){
        if (getNetAmount()==null){
            return false;
        }
        BigDecimal invoicedNetAmount = BigDecimal.ZERO;
        for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByOrderItem(this)){
            if (link.getInvoiceItem().getNetAmount()!=null) {
                invoicedNetAmount = invoicedNetAmount.add(link.getInvoiceItem().getNetAmount());
            }
        }
        return invoicedNetAmount.abs().compareTo(getNetAmount().abs()) >= 0 ? true : false;

    }

    @Programmatic
    private boolean isImmutable(){
        return getOrdr().isImmutable() || isLinkedToInvoiceItem();
    }

    private boolean isLinkedToInvoiceItem(){
        if (orderItemInvoiceItemLinkRepository.findByOrderItem(this).size()>0){
            return true;
        }
        return false;
    }

    @Programmatic
    public String reasonIncomplete(){
        StringBuffer buffer = new StringBuffer();
        if (getDescription()==null){
            buffer.append("description, ");
        }
        if (getCharge()==null){
            buffer.append("charge, ");
        }
        if (getStartDate()==null){
            buffer.append("start date, ");
        }
        if (getEndDate()==null){
            buffer.append("end date, ");
        }
        if (getNetAmount()==null){
            buffer.append("net amount, ");
        }
        if (getGrossAmount()==null){
            buffer.append("gross amount, ");
        }

        return buffer.length() == 0 ? null : buffer.toString();
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
    public Order removeItem(){
        Order order = getOrdr();
        repositoryService.removeAndFlush(this);
        return order;
    }

    public String disableRemoveItem(){
        return isImmutable() ? itemImmutableReason() : null;
    }

    private String itemImmutableReason(){
        if (isLinkedToInvoiceItem()){
            return "This order item is linked to an invoice item";
        }
        return "The order cannot be changed";
    }

    @Inject
    public OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private BudgetItemChooser budgetItemChooser;

}
