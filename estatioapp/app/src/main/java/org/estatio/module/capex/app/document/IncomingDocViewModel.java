package org.estatio.module.capex.app.document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.hint.HintStore;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.estatio.module.asset.dom.OwnershipType;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.ownership.FixedAssetOwnership;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.capex.dom.documents.BudgetItemChooser;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.policy.ViewModelWrapper;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.dom.utils.IBANValidator;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.tax.dom.Tax;

import lombok.Getter;
import lombok.Setter;

@XmlTransient // abstract class so do not map
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IncomingDocViewModel<T> implements HintStore.HintIdProvider, ViewModelWrapper<T> {


    public IncomingDocViewModel() {}

    public IncomingDocViewModel(final Document document) {
        this.document = document;
    }

    public String title() {
        return getDocument().getName();
    }


    @Getter @Setter
    protected Document document;

    public DocumentType getType() {
        return getDocument().getType();
    }

    public DateTime getCreatedAt() {
        return getDocument().getCreatedAt();
    }



    @org.apache.isis.applib.annotation.Property(hidden = Where.ALL_TABLES)
    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    public Blob getBlob() {
        return getDocument() != null ? getDocument().getBlob() : null;
    }

    /**
     * For view models with inline property edits, allows the focus to stay on the same field after OK.
     */
    @Override
    public String hintId() {
        return  bookmarkService2.bookmarkFor(getDocument()).toString();
    }



    @Programmatic
    public abstract void setDomainObject(T t);

    /**
     * Optional, the (categorisation) task (ie, just completed) that was used to create the view model.
     *
     * <p>
     *     Used in order to advance to next task after this has been classified.
     * </p>
     */
    @XmlElement(required = false)
    @Setter @Getter @Nullable
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    private Task originatingTask;

    @Programmatic
    public void modifyProperty(final Property property) {
        setProperty(property);
        deriveBuyer();
    }

    private void deriveBuyer(){
        Party ownerCandidate = null;
        if (hasProperty()){
            for (FixedAssetOwnership fos: getProperty().getOwners()){
                if (fos.getOwnershipType()== OwnershipType.FULL){
                    ownerCandidate = fos.getOwner();
                    continue;
                }
            }
            // temporary extra search until fixed asset ownership is fully in use
            if (ownerCandidate == null && getProperty().ownerCandidates().size() > 0) {
                ownerCandidate = getProperty().ownerCandidates().get(0).getParty();
            }
        }
        setBuyer(ownerCandidate);
    }

    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    @org.apache.isis.applib.annotation.PropertyLayout(named = "ECP (as buyer)")
    private Party buyer;

    public List<Party> autoCompleteBuyer(@MinLength(3) final String searchPhrase){
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }
    public String validateBuyer(final Party party){
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }

    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED)
    @org.apache.isis.applib.annotation.PropertyLayout(named = "Supplier")
    private Party seller;
    // use of modify so can be overridden on IncomingInvoiceViewmodel

    @ActionLayout(named = "Edit Supplier")
    public IncomingDocViewModel editSeller(final Party supplier, final boolean createRoleIfRequired) {
        setSeller(supplier);
        if(createRoleIfRequired) {
            partyRoleRepository.findOrCreate(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        onEditSeller(supplier);
        return this;
    }

    protected void onEditSeller(final Party seller){
    }
    public String validateEditSeller(final Party party, final boolean createRoleIfRequired){
        if(!createRoleIfRequired) {
            // requires that the supplier already has this role
            return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return null;
    }



    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Create Supplier")
    public IncomingDocViewModel createSeller(
            final String name,
            final Country country,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String ibanNumber) {
        Organisation organisation = organisationRepository
                .newOrganisation(null, true, name, country);
        setSeller(organisation);
        if (ibanNumber != null) {
            bankAccountRepository.newBankAccount(organisation, ibanNumber, null);
        }
        onCreateSeller(organisation);
        return this;
    }

    public String validateCreateSeller(
            final String name,
            final Country country,
            final String ibanNumber){
        if (ibanNumber != null && !IBANValidator.valid(ibanNumber)){
            return String.format("%s is not a valid iban number", ibanNumber);
        }
        return null;
    }

    protected void onCreateSeller(final Party seller){
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private String description;

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED)
    private Charge charge;

    @MemberOrder(name = "charge", sequence = "1")
    @ActionLayout(promptStyle = PromptStyle.INLINE)
    public T editCharge(@Nullable final Charge charge) {
        setCharge(charge);
        return (T) this;
    }
    public List<Charge> autoComplete0EditCharge(@MinLength(3) String search){
        return chargeRepository.findByApplicabilityAndMatchOnReferenceOrName(search, Applicability.INCOMING);
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Property property;

    public List<Property> choicesProperty(){
        List<Property> result = new ArrayList<>();
        if (hasBuyer()) {
            for (FixedAssetRole role : fixedAssetRoleRepository.findByPartyAndType(getBuyer(), FixedAssetRoleTypeEnum.PROPERTY_OWNER)){
                if (role.getAsset().getClass().isAssignableFrom(Property.class)) {
                    result.add((Property) role.getAsset());
                }
            }
        }
        return result.size()>0 ? result : propertyRepository.allProperties();
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Project project;

    public List<Project> choicesProject(){
        return getProperty()==null ? projectRepository.listAll() : projectRepository.findByFixedAsset(getProperty());
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private BudgetItem budgetItem;
    public void modifyBudgetItem(final BudgetItem budgetItem) {
        setBudgetItem(budgetItem);
        derivePeriodFromBudgetItem();
        deriveChargeFromBudgetItem();
    }

    public List<BudgetItem> choicesBudgetItem(){
        return budgetItemChooser.choicesBudgetItemFor(getProperty(), getCharge());
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingDocViewModel createBudgetItem(final Budget budget, final Charge charge){
        budgetItemRepository.findOrCreateBudgetItem(budget, charge);
        deriveChargeFromBudgetItem();
        derivePeriodFromBudgetItem();
        return this;
    }

    public List<Budget> choices0CreateBudgetItem(){
        if (hasProperty()){
            return budgetRepository.findByProperty(getProperty());
        }
        return budgetRepository.allBudgets();
    }

    public List<Charge> choices1CreateBudgetItem(){
        if (hasCharge()){
            return Arrays.asList(getCharge());
        }
        return chargeRepository.allIncoming();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingDocViewModel createNextBudget(final Budget previousBudget){
        previousBudget.createNextBudget();
        return this;
    }

    public List<Budget> choices0CreateNextBudget(){
        if (hasProperty()){
            return budgetRepository.findByProperty(getProperty());
        }
        return budgetRepository.allBudgets();
    }

    public String validateCreateNextBudget(final Budget previousBudget){
        return previousBudget.validateCreateNextBudget();
    }

    //region > period (prop)

    @XmlElement(required = true)
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private String period;

    public String validatePeriod(final String period) {
        if (period==null) return null; // period is optional
        return PeriodUtil.reasonInvalidPeriod(period);
    }

    protected LocalDate getStartDateFromPeriod() {
        return startDateFromPeriod(getPeriod());
    }

    protected LocalDate getEndDateFromPeriod() {
        return endDateFromPeriod(getPeriod());
    }

    private static LocalDate startDateFromPeriod(final String period) {
        LocalDateInterval localDateInterval = fromPeriod(period);
        return localDateInterval != null ? localDateInterval.startDate() : null;
    }

    private static LocalDate endDateFromPeriod(final String period) {
        LocalDateInterval localDateInterval = fromPeriod(period);
        return localDateInterval != null ? localDateInterval.endDate() : null;
    }

    private static LocalDateInterval fromPeriod(final String period) {
        return period != null
                ? PeriodUtil.yearFromPeriod(period)
                : null;
    }

    /**
     * For conveniences of subclasses, reciprocal to {@link #getStartDateFromPeriod()} and {@link #getEndDateFromPeriod()}.
     */
    protected static String periodFrom(final LocalDate startDate, final LocalDate endDate) {
        LocalDateInterval ldi = LocalDateInterval
                .including(startDate, endDate);
        return PeriodUtil.periodFromInterval(ldi);
    }


    //endregion

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private BigDecimal netAmount;
    @Digits(integer=13, fraction = 2)
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    public void modifyNetAmount(BigDecimal netAmount) {
        setNetAmount(netAmount);
        calculateVat();
        determineAmounts();
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private BigDecimal vatAmount;
    @Digits(integer=13, fraction = 2)
    public BigDecimal getVatAmount() {
        return vatAmount;
    }
    public void modifyVatAmount(BigDecimal vatAmount) {
        setVatAmount(vatAmount);
        calculateVat();
        determineAmounts();
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Tax tax;
    public void modifyTax(Tax tax) {
        setTax(tax);
        calculateVat();
        determineAmounts();
    }

    @XmlElement(required = false) @Nullable
    @Setter @Getter
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    @PropertyLayout(promptStyle = PromptStyle.INLINE)
    private BigDecimal grossAmount;
    @Digits(integer=13, fraction = 2)
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }
    public void modifyGrossAmount(BigDecimal grossAmount) {
        setGrossAmount(grossAmount);
        if (!hasNetAmount()){
            final BigDecimal valueToUse = getVatAmount()!=null ? grossAmount.subtract(getVatAmount()):getGrossAmount();
            setNetAmount(valueToUse);
        }
        calculateVat();
        determineAmounts();
    }

    // ////////////////////////////////////


    public IncomingDocViewModel changeItemDetails(
            final String description,
            @Digits(integer=13, fraction = 2)
            final BigDecimal netAmount,
            @Digits(integer=13, fraction = 2)
            @Nullable
            final BigDecimal vatAmount,
            @Nullable
            final Tax tax,
            @Digits(integer=13, fraction = 2)
            @Nullable
            final BigDecimal grossAmount
    ){
        setDescription(description);
        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setTax(tax);
        setGrossAmount(grossAmount);
        calculateVat();
        determineAmounts();
        return this;
    }

    public String default0ChangeItemDetails(){
        return getDescription();
    }

    public BigDecimal default1ChangeItemDetails(){
        return getNetAmount();
    }

    public BigDecimal default2ChangeItemDetails(){
        return getVatAmount();
    }

    public Tax default3ChangeItemDetails(){
        return getTax();
    }

    public BigDecimal default4ChangeItemDetails(){
        return getGrossAmount();
    }

    public String disableChangeItemDetails() {
        return reasonNotEditableIfAny();
    }

    // ////////////////////////////////////

    void determineAmounts(){
        if (hasVatAmount() && hasNetAmount() && !hasGrossAmount()){
            setGrossAmount(getNetAmount().add(getVatAmount()));
            return;
        }

        if (hasVatAmount() && hasGrossAmount() && !hasNetAmount()){
            setNetAmount(getGrossAmount().subtract(getVatAmount()));
            return;
        }

        if (hasNetAmount() && hasGrossAmount() && !hasVatAmount()){
            setVatAmount(getGrossAmount().subtract(getNetAmount()));
            return;
        }

    }

    void calculateVat(){
        if (hasTax() && hasNetAmount() && !hasVatAmount() && !hasGrossAmount()){
            BigDecimal grossAmount = getTax().grossFromNet(getNetAmount(), clockService.now());
            setVatAmount(grossAmount.subtract(getNetAmount()));
            return;
        }
    }

    protected void derivePeriodFromBudgetItem(){
        if (hasBudgetItem()){
            setPeriod(String.valueOf(getBudgetItem().getBudget().getBudgetYear()));
        }
    }

    protected void deriveChargeFromBudgetItem(){
        if (hasBudgetItem()){
            setCharge(getBudgetItem().getCharge());
        }
    }

    protected boolean hasTax(){
        return getTax() != null;
    }
    protected boolean hasNetAmount(){
        return getNetAmount() != null;
    }
    protected boolean hasVatAmount(){
        return getVatAmount() != null;
    }
    protected boolean hasGrossAmount(){
        return getGrossAmount() != null;
    }
    protected boolean hasCharge(){
        return getCharge() != null;
    }
    protected boolean hasBuyer(){
        return getBuyer() != null;
    }
    protected boolean hasSeller(){
        return getSeller() != null;
    }
    protected boolean hasProperty(){
        return getProperty() != null;
    }
    protected boolean hasProject(){
        return getProject() != null;
    }
    protected boolean hasBudgetItem(){
        return getBudgetItem() != null;
    }
    protected boolean hasPeriod(){
        return getPeriod() != null;
    }
    protected boolean hasDescription(){
        return getDescription() != null;
    }



    /////////////////////////////////

    /**
     * Mandatory hook - to disable actions that change the state.
     * @return
     */
    protected abstract String reasonNotEditableIfAny();


    @XmlTransient
    @Inject
    protected BookmarkService2 bookmarkService2;

    @Inject
    @XmlTransient
    public OrganisationRepository organisationRepository;

    @Inject
    @XmlTransient
    ChargeRepository chargeRepository;

    @Inject
    @XmlTransient
    ClockService clockService;

    @Inject
    @XmlTransient
    PropertyRepository propertyRepository;

    @Inject
    @XmlTransient
    FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject
    @XmlTransient
    ProjectRepository projectRepository;

    @Inject
    @XmlTransient
    BudgetRepository budgetRepository;

    @Inject
    @XmlTransient
    BudgetItemRepository budgetItemRepository;

    @Inject
    @XmlTransient
    public BankAccountRepository bankAccountRepository;

    @Inject
    @XmlTransient
    protected PaperclipRepository paperclipRepository;

    @XmlTransient
    @Inject
    BudgetItemChooser budgetItemChooser;

    @XmlTransient
    @Inject
    PartyRoleRepository partyRoleRepository;

    @XmlTransient
    @Inject
    PartyRepository partyRepository;


}
