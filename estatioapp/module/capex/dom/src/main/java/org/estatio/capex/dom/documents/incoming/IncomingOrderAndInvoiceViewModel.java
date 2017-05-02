package org.estatio.capex.dom.documents.incoming;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleRepository;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.tax.Tax;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@XmlTransient // abstract class so do not map
@XmlAccessorType(XmlAccessType.FIELD)
@Setter @Getter
public abstract class IncomingOrderAndInvoiceViewModel extends HasDocumentAbstract {

    public IncomingOrderAndInvoiceViewModel() {}

    public IncomingOrderAndInvoiceViewModel(final Document document, final FixedAsset fixedAsset) {
        super(document);
        this.fixedAsset = fixedAsset;
    }

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Organisation buyer;

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Organisation seller;


    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public IncomingOrderAndInvoiceViewModel createSeller(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION, optionality = Optionality.OPTIONAL) String reference,
            final boolean useNumeratorForReference,
            final String name,
            final Country country) {
        Organisation organisation = organisationRepository
                .newOrganisation(reference, useNumeratorForReference, name, country);
        setSeller(organisation);
        return this;
    }

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private String description;

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Charge charge;
    public List<Charge> choicesCharge(){
        return chargeRepository.allIncoming();
    }


    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private FixedAsset<?> fixedAsset;

    public List<Property> choicesFixedAsset(){
        List<Property> result = new ArrayList<>();
        if (getBuyer()!=null) {
            for (FixedAssetRole role : fixedAssetRoleRepository.findByPartyAndType(getBuyer(), FixedAssetRoleType.PROPERTY_OWNER)){
                result.add((Property) role.getAsset());
            }
        }
        return result.size()>0 ? result : propertyRepository.allProperties();
    }

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Project project;

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private String period;

    public String validatePeriod(final String period) {
        return !PeriodUtil.isValidPeriod(period)
                ? "Not a valid period; use four digits of the year with optional prefix F for a financial year (for example: F2017)"
                : null;
    }

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

    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    private Tax tax;
    public void modifyTax(Tax tax) {
        setTax(tax);
        calculateVat();
        determineAmounts();
    }


    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    @PropertyLayout(promptStyle = PromptStyle.INLINE)
    private BigDecimal grossAmount;
    @Digits(integer=13, fraction = 2)
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }
    public void modifyGrossAmount(BigDecimal grossAmount) {
        setGrossAmount(grossAmount);
        if (getNetAmount()==null){
            final BigDecimal valueToUse = getVatAmount()!=null ? grossAmount.subtract(getVatAmount()):getGrossAmount();
            setNetAmount(valueToUse);
        }
        calculateVat();
        determineAmounts();
    }

    // ////////////////////////////////////

    public IncomingOrderAndInvoiceViewModel changeDimensions(
            @Parameter(optionality = Optionality.OPTIONAL)
            final Charge charge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Property property,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Project project,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String period
    ){
        setCharge(charge);
        setFixedAsset(property);
        setProject(project);
        setPeriod(period);
        return this;
    }

    public Charge default0ChangeDimensions(){
        return getCharge();
    }

    public Property default1ChangeDimensions(){
        return (Property) getFixedAsset();
    }

    public Project default2ChangeDimensions(){
        return getProject();
    }

    public String default3ChangeDimensions(){
        return getPeriod();
    }

    public List<Property> choices1ChangeDimensions() {
        return choicesFixedAsset();
    }

    public String validateChangeDimensions(
            final Charge charge,
            final Property property,
            final Project project,
            final String period
    ) {
        return validatePeriod(period);
    }

    // ////////////////////////////////////

    public IncomingOrderAndInvoiceViewModel changeItemDetails(
            final String description,
            final BigDecimal netAmount,
            @Nullable
            final BigDecimal vatAmount,
            @Nullable
            final Tax tax,
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

    // ////////////////////////////////////

    void determineAmounts(){
        if (hasVat() && hasNet() && !hasGross()){
            setGrossAmount(getNetAmount().add(getVatAmount()));
            return;
        }

        if (hasVat() && hasGross() && !hasNet()){
            setNetAmount(getGrossAmount().subtract(getVatAmount()));
            return;
        }

        if (hasNet() && hasGross() && !hasVat()){
            setVatAmount(getGrossAmount().subtract(getNetAmount()));
            return;
        }

    }

    void calculateVat(){
        if (hasTax() && hasNet() && !hasVat() && !hasGross()){
            BigDecimal grossAmount = getTax().grossFromNet(getNetAmount(), clockService.now());
            setVatAmount(grossAmount.subtract(getNetAmount()));
            return;
        }
    }

    protected boolean hasTax(){
        return getTax() != null;
    }
    protected boolean hasNet(){
        return getNetAmount() != null;
    }
    protected boolean hasVat(){
        return getVatAmount() != null;
    }
    protected boolean hasGross(){
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
    protected boolean hasFixedAsset(){
        return getFixedAsset() != null;
    }
    protected boolean hasProject(){
        return getProject() != null;
    }
    protected boolean hasPeriod(){
        return getPeriod() != null;
    }
    protected boolean hasDescription(){
        return getDescription() != null;
    }

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE) // to prevent from being shown in UI
    @Setter(AccessLevel.NONE)
    OrganisationRepository organisationRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ChargeRepository chargeRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ClockService clockService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    PropertyRepository propertyRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    FixedAssetRoleRepository fixedAssetRoleRepository;

}
