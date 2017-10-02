package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.validation.constraints.Digits;

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
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemInvoiceItemLinkValidationService;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Applicability;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.utils.FinancialAmountUtil;
import org.estatio.dom.utils.ReasonBuffer2;
import org.estatio.tax.dom.Tax;
import org.estatio.tax.dom.TaxRate;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE

        // unused since rolled-up to superclass:
        //,schema = "dbo"
        //,table = "IncomingInvoiceItem"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Queries({
        @Query(
                name = "findByInvoiceAndChargeAndSequence", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoiceItem "
                        + "WHERE invoice == :invoice "
                        + "   && charge == :charge "
                        + "   && sequence == :sequence "),
        @Query(
                name = "findByProjectAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoiceItem "
                        + "WHERE project == :project "
                        + "   && charge == :charge "),
        @Query(
                name = "findByProject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoiceItem "
                        + "WHERE project == :project "),
        @Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoiceItem "
                        + "WHERE invoice.seller == :seller "),
        @Query(
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoiceItem "
                        + "WHERE budgetItem == :budgetItem ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "incomingInvoice.IncomingInvoiceItem"
)
@javax.jdo.annotations.Discriminator(
        "incomingInvoice.IncomingInvoiceItem"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class IncomingInvoiceItem extends InvoiceItem<IncomingInvoiceItem> implements FinancialItem {

    public IncomingInvoiceItem(){}

    public IncomingInvoiceItem(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final IncomingInvoiceType incomingInvoiceType,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final org.estatio.dom.asset.Property property,
            final Project project,
            final BudgetItem budgetItem){
        super(invoice);
        this.incomingInvoiceType = incomingInvoiceType;

        setSequence(sequence);

        setCharge(charge);
        setDescription(description);

        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setGrossAmount(grossAmount);

        setDueDate(dueDate);
        setStartDate(startDate);
        setEndDate(endDate);

        setTax(tax);
        setFixedAsset(property);
        setProject(project);
        setBudgetItem(budgetItem);

    }


    @Override
    @Programmatic
    public BigDecimal value() {
        return getNetAmount();
    }

    @Override
    public FinancialItemType getType() {
        return FinancialItemType.INVOICED;
    }




    /**
     * Typically the same as the {@link IncomingInvoice#getType() type} defined by the {@link #getInvoice() parent}
     * {@link IncomingInvoice invoice}, but can be overridden if necessary.
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    private IncomingInvoiceType incomingInvoiceType;

    public void setIncomingInvoiceType(final IncomingInvoiceType incomingInvoiceType) {
        this.incomingInvoiceType = invalidateApprovalIfDiffer(this.incomingInvoiceType, incomingInvoiceType);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editIncomingInvoiceType(final IncomingInvoiceType incomingInvoiceType) {
        setIncomingInvoiceType(incomingInvoiceType);
        return this;
    }

    public IncomingInvoiceType default0EditIncomingInvoiceType(){
        return getIncomingInvoiceType();
    }

    public String disableEditIncomingInvoiceType(){
        return getIncomingInvoice().reasonDisabledDueToStateStrict();
    }




    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private FixedAsset fixedAsset;

    public void setFixedAsset(final FixedAsset fixedAsset) {
        this.fixedAsset = invalidateApprovalIfDiffer(this.fixedAsset, fixedAsset);
    }

    @Getter @Setter
    @Column(allowsNull = "true", name="projectId")
    @Property(hidden = Where.REFERENCES_PARENT)
    private Project project;

    public void setProject(final Project project) {
        this.project = invalidateApprovalIfDiffer(this.project, project);
    }


    @Getter @Setter
    @Column(allowsNull = "true", name="budgetItemId")
    @Property(hidden = Where.REFERENCES_PARENT)
    private BudgetItem budgetItem;

    public void setBudgetItem(final BudgetItem budgetItem) {
        this.budgetItem = invalidateApprovalIfDiffer(this.budgetItem, budgetItem);
    }

    @Override
    public void setCharge(final Charge charge) {
        super.setCharge(invalidateApprovalIfDiffer(getCharge(), charge));
    }

    @Override
    public void setDescription(final String description) {
        super.setDescription(invalidateApprovalIfDiffer(getDescription(), description));
    }

    @Override
    public void setQuantity(final BigDecimal quantity) {
        super.setQuantity(invalidateApprovalIfDiffer(getQuantity(), quantity));
    }

    @Override
    public void setNetAmount(final BigDecimal netAmount) {
        super.setNetAmount(invalidateApprovalIfDiffer(getNetAmount(), netAmount));
    }

    @Override
    public void setVatAmount(final BigDecimal vatAmount) {
        super.setVatAmount(invalidateApprovalIfDiffer(getVatAmount(), vatAmount));
    }

    @Override
    public void setGrossAmount(final BigDecimal grossAmount) {
        super.setGrossAmount(invalidateApprovalIfDiffer(getGrossAmount(), grossAmount));
    }

    @Override
    public void setTax(final Tax tax) {
        super.setTax(invalidateApprovalIfDiffer(getTax(), tax));
    }

    @Override
    public void setDueDate(final LocalDate dueDate) {
        super.setDueDate(invalidateApprovalIfDiffer(getDueDate(), dueDate));
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        super.setStartDate(invalidateApprovalIfDiffer(getStartDate(), startDate));
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        super.setEndDate(invalidateApprovalIfDiffer(getEndDate(), endDate));
    }

    @Override
    public void setEffectiveStartDate(final LocalDate effectiveStartDate) {
        super.setEffectiveStartDate(invalidateApprovalIfDiffer(getEffectiveStartDate(), effectiveStartDate));
    }

    @Override
    public void setEffectiveEndDate(final LocalDate effectiveEndDate) {
        super.setEffectiveEndDate(invalidateApprovalIfDiffer(getEffectiveEndDate(), effectiveEndDate));
    }

    @Override
    public void setTaxRate(final TaxRate taxRate) {
        super.setTaxRate(invalidateApprovalIfDiffer(getTaxRate(), taxRate));
    }

    private IncomingInvoice getIncomingInvoice() {
        return (IncomingInvoice) getInvoice();
    }

    @Programmatic
    public String getPeriod(){
        return PeriodUtil.periodFromInterval(getInterval());
    }

    /**
     * The date that this line item was reported to external auditors, thereby should be treated as immutable.
     */
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate reportedDate;

    /**
     * Whether this item is a reversal of a previous invoice item, meaning that it should be treated as immutable.
     *
     * Note that reversals are only required on items that have become immutable, typically by virtue of having been
     * {@link #getReportedDate() reported} to external auditors.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", name = "reversalOfInvoiceItemId")
    @Property
    @Getter @Setter
    private IncomingInvoiceItem reversalOf;


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoiceItem updateAmounts(
            @Digits(integer=13, fraction = 2)
            final BigDecimal netAmount,
            @Nullable
            @Digits(integer=13, fraction = 2)
            final BigDecimal vatAmount,
            @Digits(integer=13, fraction = 2)
            final BigDecimal grossAmount,
            @Nullable
            final Tax tax){
        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setGrossAmount(grossAmount);
        setTax(tax);
        IncomingInvoice invoice = (IncomingInvoice) getInvoice();
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
        return getIncomingInvoice().isImmutableDueToState() ? itemImmutableReason() : null;
    }

    public String validate0UpdateAmounts(final BigDecimal proposedNetAmount) {
        if(proposedNetAmount == null) return null; // shouldn't occur, I think.
        final BigDecimal netAmountLinked = orderItemInvoiceItemLinkRepository
                .calculateNetAmountLinkedFromInvoiceItem(this);
        if(proposedNetAmount.compareTo(netAmountLinked) < 0) {
            return "Cannot be less than the amount already linked (" + netAmountLinked + ")";
        }
        return null;
    }
    public String validateUpdateAmounts(
            final BigDecimal proposedNetAmount,
            final BigDecimal proposedVatAmount,
            final BigDecimal proposedGrossAmount,
            final Tax tax) {
        if(proposedNetAmount == null || proposedGrossAmount == null) return null; // shouldn't occur, I think.
        if(proposedNetAmount.compareTo(proposedGrossAmount) > 0) {
            return "Net amount cannot be greater than the gross amount";
        }
        return null;
    }





    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editDescription(
                                    @ParameterLayout(multiLine = DescriptionType.Meta.MULTI_LINE)
                                    final String description) {
        setDescription(description);
        return this;
    }

    public String default0EditDescription(){
        return getDescription();
    }

    public String disableEditDescription(){
        return getIncomingInvoice().isImmutableDueToState() ? itemImmutableReason() : null;
    }





    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editDueDate(final LocalDate dueDate){
        setDueDate(dueDate);
        return this;
    }

    public LocalDate default0EditDueDate(){
        return getDueDate();
    }

    public String disableEditDueDate(){
        return getIncomingInvoice().isImmutableDueToState() ? itemImmutableReason() : null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editCharge(@Nullable final Charge charge) {
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
        return chargeIsImmutableReason();
    }





    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editFixedAsset(
            @Nullable
            final org.estatio.dom.asset.Property property){
        setFixedAsset(property);
        return this;
    }

    public org.estatio.dom.asset.Property default0EditFixedAsset(){
        return (org.estatio.dom.asset.Property) getFixedAsset();
    }

    public String disableEditFixedAsset(){
        return fixedAssetIsImmutableReason();
    }





    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editProject(
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
        return projectIsImmutableReason();
    }





    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public IncomingInvoiceItem editBudgetItem(
            @Nullable
            final BudgetItem budgetItem){
        setBudgetItem(budgetItem);
        if (budgetItem!=null) setCharge(budgetItem.getCharge());
        if (budgetItem!=null) setFixedAsset(budgetItem.getBudget().getProperty());
        return this;
    }

    public BudgetItem default0EditBudgetItem(){
        return getBudgetItem();
    }

    public List<BudgetItem> choices0EditBudgetItem() {
        return budgetItemChooser.choicesBudgetItemFor((org.estatio.dom.asset.Property) getFixedAsset(), getCharge());
    }

    public String disableEditBudgetItem(){
        return budgetItemIsImmutableReason();
    }

    public IncomingInvoiceItem editPeriod(@Nullable final String period){
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

    private String itemImmutableReason(){
        return "The invoice can't be changed";
    }

    private String chargeIsImmutableReason(){

        // nb: dimensions *are* allowed to change irrespective of state,
        // so we don't check IncomingInvoice#isImmutableDueToState()

        final ReasonBuffer2 buf =
                ReasonBuffer2.prefix("Charge cannot be changed because invoice item");

        buf.appendOnCondition(this.getReportedDate() != null, "has already been reported");
        buf.appendOnCondition(this.getReversalOf() != null, "is a reversal of another item");

        buf.appendOnCondition(this.isLinkedToOrderItem(), "item is linked to an order");
        buf.appendOnCondition(this.getBudgetItem()!=null, "is linked to a budget" );

        return buf.getReason();
    }

    private String fixedAssetIsImmutableReason(){

        // nb: dimensions *are* allowed to change irrespective of state,
        // so we don't check IncomingInvoice#isImmutableDueToState()

        final ReasonBuffer2 buf =
                ReasonBuffer2.prefix("Fixed asset cannot be changed because invoice item");

        buf.appendOnCondition(this.getReportedDate() != null, "has already been reported");
        buf.appendOnCondition(this.getReversalOf() != null, "is a reversal of another item");

        buf.appendOnCondition(this.isLinkedToOrderItem(), "is linked to an order");
        buf.appendOnCondition(this.getBudgetItem()!=null, "is linked to a budget");
        buf.appendOnCondition(this.getProject()!=null, "is linked to a project");

        return buf.getReason();
    }

    private String budgetItemIsImmutableReason(){

        // nb: dimensions *are* allowed to change irrespective of state,
        // so we don't check IncomingInvoice#isImmutableDueToState()

        final ReasonBuffer2 buf =
                ReasonBuffer2.prefix("Budget item cannot be changed because invoice item");

        buf.appendOnCondition(this.getReportedDate() != null, "has already been reported");
        buf.appendOnCondition(this.getReversalOf() != null, "is a reversal of another item");

        buf.appendOnCondition(
                !hasType(IncomingInvoiceType.SERVICE_CHARGES),
                "parent invoice is not for service charges");
        buf.appendOnCondition(this.isLinkedToOrderItem(), "is linked to an order");

        return buf.getReason();
    }

    private boolean hasType(final IncomingInvoiceType serviceCharges) {
        final IncomingInvoiceType incomingInvoiceType = this.getIncomingInvoice().getType();
        return incomingInvoiceType != null && incomingInvoiceType == serviceCharges;
    }

    private String projectIsImmutableReason(){

        // nb: dimensions *are* allowed to change irrespective of state,
        // so we don't check IncomingInvoice#isImmutableDueToState()

        final ReasonBuffer2 buf =
                ReasonBuffer2.prefix("Project cannot be changed because invoice item");

        buf.appendOnCondition(this.getReportedDate() != null, "has already been reported");
        buf.appendOnCondition(this.getReversalOf() != null, "is a reversal of another item");

        buf.appendOnCondition(this.isLinkedToOrderItem(), "is linked to an order");

        return buf.getReason();
    }

    boolean isLinkedToOrderItem(){
        final List<OrderItemInvoiceItemLink> numberOfLinks =
                orderItemInvoiceItemLinkRepository.findByInvoiceItem(this);
        return numberOfLinks.size() > 0;
    }






    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Invoice removeItem(){
        IncomingInvoice invoice = (IncomingInvoice) getInvoice();
        if (isLinkedToOrderItem()){
            for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByInvoiceItem(this)){
                repositoryService.removeAndFlush(link);
            }
        }
        repositoryService.removeAndFlush(this);
        return invoice;
    }

    public String disableRemoveItem(){
        return getIncomingInvoice().isImmutableDueToState() ? itemImmutableReason() : null;
    }

    @Programmatic
    public String reasonIncomplete(){
        return new Validator()
                    .checkNotNull(getIncomingInvoiceType(), "incoming invoice type")
                    .checkNotNull(getStartDate(), "start date")
                    .checkNotNull(getEndDate(), "end date")
                    .checkNotNull(getNetAmount(), "net amount")
                    .checkNotNull(getVatAmount(), "vat amount")
                    .checkNotNull(getGrossAmount(), "gross amount")
                    .checkNotNull(getCharge(), "charge")
                    .validateForIncomingInvoiceType(this)
                    .getResult();
    }

    /**
     * has final modifier so cannot be mocked out.
     */
    final <T> T invalidateApprovalIfDiffer(final T previousValue, final T newValue) {
        if (!Objects.equals(previousValue, newValue)) {
            invalidateApproval();
        }
        return newValue;
    }

    void invalidateApproval() {
        getIncomingInvoice().invalidateApproval();
    }

    static class Validator {

        public Validator(){
            this.result = null;
        }

        @Setter
        String result;

        String getResult(){
            return result!=null ? result.concat(" required") : null;
        }

        Validator checkNotNull(Object mandatoryProperty, String propertyName){
            if (mandatoryProperty == null){
                setResult(result==null ? propertyName : result.concat(", ").concat(propertyName));
            }
            return this;
        }

        Validator validateForIncomingInvoiceType(IncomingInvoiceItem incomingInvoiceItem){
            if (incomingInvoiceItem == null) return this;
            if (incomingInvoiceItem.getIncomingInvoiceType() == null) return this;

            String message;
            switch (incomingInvoiceItem.getIncomingInvoiceType()){

                case CAPEX:
                    message = "project (capex)";
                    if (incomingInvoiceItem.getProject()==null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "fixed asset";
                    if (incomingInvoiceItem.getFixedAsset()==null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "removal of budget item (only applicable for service charges)";
                    if (incomingInvoiceItem.getBudgetItem()!=null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                break;

                case SERVICE_CHARGES:
                    message = "budget item (service charges)";
                    if (incomingInvoiceItem.getBudgetItem()==null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "fixed asset";
                    if (incomingInvoiceItem.getFixedAsset()==null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "equal charge on budget item and invoice item";
                    if (incomingInvoiceItem.getBudgetItem()!=null && incomingInvoiceItem.getCharge()!=null){
                        if (!incomingInvoiceItem.getBudgetItem().getCharge().equals(incomingInvoiceItem.getCharge())){
                            setResult(result==null ? message : result.concat(", ").concat(message));
                        }
                    }
                    message = "removal of project (only applicable for capex)";
                    if (incomingInvoiceItem.getProject()!=null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    break;

                case PROPERTY_EXPENSES:
                    message = "fixed asset";
                    if (incomingInvoiceItem.getFixedAsset()==null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "removal of budget item (only applicable for service charges)";
                    if (incomingInvoiceItem.getBudgetItem()!=null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "remove project (only applicable for capex)";
                    if (incomingInvoiceItem.getProject()!=null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    break;

                default:
                    message = "removal of budget item (only applicable for service charges)";
                    if (incomingInvoiceItem.getBudgetItem()!=null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
                    }
                    message = "removal of project (only applicable for capex)";
                    if (incomingInvoiceItem.getProject()!=null){
                        setResult(result==null ? message : result.concat(", ").concat(message));
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

    @Programmatic
    public boolean isDiscarded(){
        if (getInvoice() == null) return false; // done in order to ease junit testing
        IncomingInvoice invoice = (IncomingInvoice) getInvoice();
        return invoice.getApprovalState()!=null ? invoice.getApprovalState()==IncomingInvoiceApprovalState.DISCARDED : false;
    }


    @Programmatic
    public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny(){
        if (orderItemInvoiceItemLinkRepository.findByInvoiceItem(this).size()==1) {
            OrderItem linkedOrderItem = orderItemInvoiceItemLinkRepository.findByInvoiceItem(this).get(0).getOrderItem();
            if (linkedOrderItem.getCharge() != null) {
                setCharge(linkedOrderItem.getCharge());
            }
            if (linkedOrderItem.getProject() != null) {
                setProject(linkedOrderItem.getProject());
            }
            if (linkedOrderItem.getProperty() != null) {
                setFixedAsset(linkedOrderItem.getProperty());
            }
        }
    }



    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    OrderItemInvoiceItemLinkValidationService linkValidationService;

    @Inject
    BudgetItemChooser budgetItemChooser;

    @Inject
    QueryResultsCache queryResultsCache;

}
