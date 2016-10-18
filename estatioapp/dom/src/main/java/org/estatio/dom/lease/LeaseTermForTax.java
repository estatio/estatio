package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "EstatioLease"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.lease.LeaseTermForTax")   // TODO: externalize mapping
@DomainObject
public class LeaseTermForTax extends LeaseTerm {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal taxableValue;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal taxValue;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 1)
    @Getter @Setter
    private BigDecimal taxPercentage;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal payableValue;

    // //////////////////////////////////////

    private boolean overridePayableValue;

    public boolean isOverridePayableValue() {
        return overridePayableValue;
    }

    public void setOverridePayableValue(final boolean overridePayableValue) {
        this.overridePayableValue = overridePayableValue;
    }

    public LeaseTermForTax changeTax(
            final BigDecimal taxPercentage,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal overridePayableValue) {
        setTaxPercentage(taxPercentage);
        setPayableValue(overridePayableValue);
        setOverridePayableValue(overridePayableValue != null);
        return this;
    }

    public BigDecimal default0ChangeTax() {
        return getTaxPercentage();
    }

    public BigDecimal default1ChangeTax() {
        return isOverridePayableValue() ? getPayableValue() : null;
    }

    // //////////////////////////////////////

    @Persistent
    private LocalDate paymentDate;

    @Column(allowsNull = "true")
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(final LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public LeaseTermForTax changePaymentDate(
            final LocalDate paymentDate) {
        setPaymentDate(paymentDate);
        return this;
    }

    public LocalDate default0ChangePaymentDate() {
        return getClockService().now();
    }

    // //////////////////////////////////////

    private BigDecimal recoverablePercentage;

    @javax.jdo.annotations.Column(scale = 1)
    public BigDecimal getRecoverablePercentage() {
        return recoverablePercentage;
    }

    public void setRecoverablePercentage(final BigDecimal recoverablePercentage) {
        this.recoverablePercentage = recoverablePercentage;
    }

    // //////////////////////////////////////

    private boolean overrideTaxValue;

    public boolean isOverrideTaxValue() {
        return overrideTaxValue;
    }

    public void setOverrideTaxValue(final boolean overrideTaxValue) {
        this.overrideTaxValue = overrideTaxValue;
    }

    // //////////////////////////////////////

    public LeaseTermForTax changeInvoicing(
            final BigDecimal recoverablePercentage,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal overrideRecoverableAmount) {
        setRecoverablePercentage(recoverablePercentage);
        setTaxValue(overrideRecoverableAmount);
        setOverrideTaxValue(overrideRecoverableAmount != null);
        return this;
    }

    public BigDecimal default0ChangeInvoicing() {
        return getTaxPercentage();
    }

    public BigDecimal default1ChangeInvoicing() {
        return isOverrideTaxValue() ? getTaxValue() : null;
    }

    // //////////////////////////////////////

    private boolean invoicingDisabled;

    public boolean isInvoicingDisabled() {
        return invoicingDisabled;
    }

    public void setInvoicingDisabled(final boolean disabledForInvoicing) {
        this.invoicingDisabled = disabledForInvoicing;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            named = "Disable Invoicing"
    )
    public LeaseTermForTax dizableInvoicing(String reason) {
        setInvoicingDisabled(true);
        return this;
    }

    public boolean hideDizableInvoicing() {
        return isInvoicingDisabled();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public LeaseTermForTax enableInvoicing(String reason) {
        setInvoicingDisabled(false);
        return this;
    }

    public boolean hideEnableInvoicing() {
        return !isInvoicingDisabled();
    }

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Getter @Setter
    private String officeName;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Getter @Setter
    private String officeCode;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Persistent
    @Getter @Setter
    private LocalDate registrationDate;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Getter @Setter
    private String registrationNumber;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

    public LeaseTermForTax changeRegistration(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate registrationDate,
            final @Parameter(optionality = Optionality.OPTIONAL) String registrationNumber,
            final @Parameter(optionality = Optionality.OPTIONAL) String officeCode,
            final @Parameter(optionality = Optionality.OPTIONAL) String officeName,
            final @ParameterLayout(multiLine = 3) @Parameter(optionality = Optionality.OPTIONAL) String description
            ) {
        setRegistrationDate(registrationDate);
        setRegistrationNumber(registrationNumber);
        setOfficeCode(officeCode);
        setOfficeName(officeName);
        setDescription(description);
        return this;
    }

    public LocalDate default0ChangeRegistration() {
        return getRegistrationDate();
    }

    public String default1ChangeRegistration() {
        return getRegistrationNumber();
    }

    public String default2ChangeRegistration() {
        return getOfficeCode();
    }

    public String default3ChangeRegistration() {
        return getOfficeName();
    }

    public String default4ChangeRegistration() {
        return getDescription();
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return valueForDate(null);
    }

    @Override
    public BigDecimal valueForDate(LocalDate dueDate) {
        if (isInvoicingDisabled()) {
            return new BigDecimal("0.00");
        }
        return getTaxValue();
    }

    @Override
    public LeaseTermValueType valueType() {
        return LeaseTermValueType.FIXED;
    }

    @Override
    protected void doInitialize() {
        final LeaseTermForTax previous = (LeaseTermForTax) this.getPrevious();
        if (previous != null) {
            setTaxPercentage(previous.getTaxPercentage());
            setRecoverablePercentage(previous.getRecoverablePercentage());
            // static data
            setOfficeCode(previous.getOfficeCode());
            setOfficeName(previous.getOfficeName());
            setRegistrationDate(previous.getRegistrationDate());
            setRegistrationNumber(previous.getRegistrationNumber());
            setDescription(previous.getDescription());
        }
    }

    @Override
    protected void doAlign() {
        if (getPaymentDate() == null) {
            final BigDecimal newTaxableValue = rentValueForDate();
            final BigDecimal newTaxPercentage = getTaxPercentage() == null ? BigDecimal.ZERO : getTaxPercentage();
            final BigDecimal newRecoverablePercentage = getRecoverablePercentage() == null ? BigDecimal.ZERO : getRecoverablePercentage();
            final BigDecimal newPayableValue = newTaxableValue.multiply(newTaxPercentage.divide(HUNDRED)).setScale(0, RoundingMode.HALF_UP).setScale(2);
            final BigDecimal newTaxValue = newPayableValue.multiply(newRecoverablePercentage).divide(HUNDRED).setScale(2, RoundingMode.HALF_UP);
            if (ObjectUtils.compare(getTaxableValue(), newTaxableValue) != 0) {
                setTaxableValue(newTaxableValue);
            }
            if (!isOverrideTaxValue() && ObjectUtils.compare(newTaxValue, getTaxValue()) != 0) {
                setTaxValue(newTaxValue);
            }
            if (!isOverridePayableValue() && ObjectUtils.compare(getPayableValue(), newPayableValue) != 0) {
                setPayableValue(newPayableValue);
            }
        }
    }

    @Programmatic
    public BigDecimal rentValueForDate() {
        BigDecimal rentValue = BigDecimal.ZERO;
        for (LeaseItemSource leaseItemSource : getLeaseItem().getSourceItems()){
            final BigDecimal valueForDate = leaseItemSource.getSourceItem().valueForDate(getStartDate());
            rentValue = rentValue.add(valueForDate == null ? BigDecimal.ZERO : valueForDate);
        }
        return rentValue;
    }

    @Override
    public void copyValuesTo(LeaseTerm target) {
        LeaseTermForTax t = (LeaseTermForTax) target;
        super.copyValuesTo(target);
        t.setOfficeCode(getOfficeCode());
        t.setOfficeName(getOfficeName());
        t.setRegistrationDate(getRegistrationDate());
        t.setRegistrationNumber(getRegistrationNumber());
        t.setDescription(getDescription());
        t.setPaymentDate(getPaymentDate());
        t.setRecoverablePercentage(getRecoverablePercentage());
        t.setTaxPercentage(getTaxPercentage());
    }

    // //////////////////////////////////////

    @Inject
    private LeaseItemRepository leaseItemRepository;

}
