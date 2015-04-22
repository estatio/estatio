package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@DomainObject
public class LeaseTermForTax extends LeaseTerm {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    // //////////////////////////////////////

    private BigDecimal taxableValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(final BigDecimal taxableValue) {
        this.taxableValue = taxableValue;
    }

    // //////////////////////////////////////

    private BigDecimal taxValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(final BigDecimal taxValue) {
        this.taxValue = taxValue;
    }

    // //////////////////////////////////////

    private BigDecimal taxPercentage;

    @javax.jdo.annotations.Column(scale = 1)
    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(final BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    // //////////////////////////////////////

    private BigDecimal payableValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getPayableValue() {
        return payableValue;
    }

    public void setPayableValue(BigDecimal payableValue) {
        this.payableValue = payableValue;
    }

    // //////////////////////////////////////

    private boolean overridePayableValue;

    public boolean isOverridePayableValue() {
        return overridePayableValue;
    }

    public void setOverridePayableValue(final boolean overridePayableValue) {
        this.overridePayableValue = overridePayableValue;
    }

    public LeaseTermForTax changeTax(
            final @ParameterLayout(named = "Tax percentage") BigDecimal taxPercentage,
            final @ParameterLayout(named = "Override payable value") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal overridePayableValue) {
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

    public LeaseTermForTax changePaymentDate(
            final @ParameterLayout(named = "Payment date") LocalDate paymentDate,
            final @ParameterLayout(named = "Are you sure?") Boolean confirm) {
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
            final @ParameterLayout(named = "Recoverable percentage") BigDecimal recoverablePercentage,
            final @ParameterLayout(named = "Override recoverable amount") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal overrideTaxValue) {
        setRecoverablePercentage(recoverablePercentage);
        setTaxValue(overrideTaxValue);
        setOverrideTaxValue(overrideTaxValue != null);
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

    public LeaseTermForTax disableInvoicing(@ParameterLayout(named = "Reason") String reason) {
        setInvoicingDisabled(true);
        return this;
    }

    public boolean hideDisableInvoicing() {
        return isInvoicingDisabled();
    }

    public LeaseTermForTax enableInvoicing(@ParameterLayout(named = "Reason") String reason) {
        setInvoicingDisabled(false);
        return this;
    }

    public boolean hideEnableInvoicing() {
        return !isInvoicingDisabled();
    }

    // //////////////////////////////////////

    private String officeName;

    @Column(allowsNull = "true")
    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(final String officeName) {
        this.officeName = officeName;
    }

    // //////////////////////////////////////

    private String officeCode;

    @Column(allowsNull = "true")
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(final String officeCode) {
        this.officeCode = officeCode;
    }

    // //////////////////////////////////////

    @Persistent
    private LocalDate registrationDate;

    @Column(allowsNull = "true")
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(final LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    // //////////////////////////////////////

    private String registrationNumber;

    @Column(allowsNull = "true")
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(final String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    // //////////////////////////////////////

    private String description;

    @Column(allowsNull = "true")
    @PropertyLayout(multiLine = 3)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    public LeaseTermForTax changeRegistration(
            final @ParameterLayout(named = "Registration date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate registrationDate,
            final @ParameterLayout(named = "Registration number") @Parameter(optionality = Optionality.OPTIONAL) String registrationNumber,
            final @ParameterLayout(named = "Office code") @Parameter(optionality = Optionality.OPTIONAL) String officeCode,
            final @ParameterLayout(named = "Office name") @Parameter(optionality = Optionality.OPTIONAL) String officeName,
            final @ParameterLayout(named = "Description", multiLine = 3) @Parameter(optionality = Optionality.OPTIONAL) String description
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
        for (LeaseItem leaseItem : leaseItems.findLeaseItemsByType(getLeaseItem().getLease(), LeaseItemType.RENT)) {
            final BigDecimal valueForDate = leaseItem.valueForDate(getStartDate());
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
    private LeaseItems leaseItems;

}
