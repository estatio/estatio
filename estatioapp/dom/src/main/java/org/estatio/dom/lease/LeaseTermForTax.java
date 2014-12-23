package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Immutable
public class LeaseTermForTax extends LeaseTerm {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    // //////////////////////////////////////

    private boolean invoicingDisabled;

    public boolean isInvoicingDisabled() {
        return invoicingDisabled;
    }

    public void setInvoicingDisabled(final boolean disabledForInvoicing) {
        this.invoicingDisabled = disabledForInvoicing;
    }

    public LeaseTermForTax disableInvoicing(@Named("Reason") String reason) {
        setInvoicingDisabled(true);
        return this;
    }

    public boolean hideDisableInvoicing() {
        return isInvoicingDisabled();
    }

    public LeaseTermForTax enableInvoicing(@Named("Reason") String reason) {
        setInvoicingDisabled(false);
        return this;
    }

    public boolean hideEnableInvoicing() {
        return !isInvoicingDisabled();
    }

    // //////////////////////////////////////

    private BigDecimal taxableValue;

    @Optional
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(final BigDecimal taxableValue) {
        this.taxableValue = taxableValue;
    }

    // //////////////////////////////////////

    private BigDecimal taxValue;

    @Optional
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(final BigDecimal taxValue) {
        this.taxValue = taxValue;
    }

    // //////////////////////////////////////

    private boolean overrideTaxValue;

    @Optional
    public boolean isOverrideTaxValue() {
        return overrideTaxValue;
    }

    public void setOverrideTaxValue(final boolean overrideTaxValue) {
        this.overrideTaxValue = overrideTaxValue;
    }

    public LeaseTermForTax overrideTaxValue(
            final @Named("Override tax value") BigDecimal overrideTaxValue) {
        setTaxValue(overrideTaxValue);
        setOverrideTaxValue(true);
        return this;
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

    private BigDecimal recoverablePercentage;

    @javax.jdo.annotations.Column(scale = 1)
    public BigDecimal getRecoverablePercentage() {
        return recoverablePercentage;
    }

    public void setRecoverablePercentage(final BigDecimal recoverablePercentage) {
        this.recoverablePercentage = recoverablePercentage;
    }

    // //////////////////////////////////////

    @Persistent
    private LocalDate paymentDate;

    @Optional
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(final LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LeaseTermForTax changePaymentDate(
            final @Named("Payment date") LocalDate paymentDate,
            final @Named("Are you sure?") Boolean confirm) {
        setPaymentDate(paymentDate);
        return this;
    }

    public LocalDate default0ChangePaymentDate() {
        return getClockService().now();
    }

    // //////////////////////////////////////

    public LeaseTermForTax changeParameters(
            final @Named("Tax percentage") @Optional BigDecimal taxPercentage,
            final @Named("Recoverable percentage") @Optional BigDecimal recoverablePercentage) {
        setTaxPercentage(taxPercentage);
        setRecoverablePercentage(recoverablePercentage);
        return this;
    }

    public BigDecimal default0ChangeParameters() {
        return getTaxPercentage();
    }

    public BigDecimal default1ChangeParameters() {
        return getRecoverablePercentage();
    }

    // //////////////////////////////////////

    private String officeName;

    @Optional
    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(final String officeName) {
        this.officeName = officeName;
    }

    // //////////////////////////////////////

    private String officeCode;

    @Optional
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(final String officeCode) {
        this.officeCode = officeCode;
    }

    // //////////////////////////////////////

    @Persistent
    private LocalDate registrationDate;

    @Optional
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(final LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    // //////////////////////////////////////

    private String registrationNumber;

    @Optional
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(final String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    // //////////////////////////////////////

    private String description;

    @Optional
    @MultiLine(numberOfLines = 3)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    public LeaseTermForTax changeRegistration(
            final @Named("Registration date") @Optional LocalDate registrationDate,
            final @Named("Registration number") @Optional String registrationNumber,
            final @Named("Office code") @Optional String officeCode,
            final @Named("Office name") @Optional String officeName,
            final @Named("Description") @Optional @MultiLine(numberOfLines = 3) String description
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
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        if (rentItem != null) {
            setTaxableValue(rentItem.valueForDate(getStartDate()));
        }
        // TODO: Disabled the calculation of tax. To be discussed with the users

        if (!isOverrideTaxValue() && getTaxableValue() != null && getTaxPercentage() != null
        // && getTaxValue() == null //TODO: we must safeguard existing values
        ) {
            BigDecimal taxFactor = getTaxPercentage().divide(HUNDRED);
            BigDecimal recoverableFactor =
                    getRecoverablePercentage().divide(HUNDRED);
            BigDecimal taxValue =
                    getTaxableValue().multiply(taxFactor).multiply(recoverableFactor).setScale(0, RoundingMode.HALF_UP);
            if (ObjectUtils.compare(taxValue, getTaxValue()) != 0) {
                setTaxValue(taxValue);
            }
        }
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

}
