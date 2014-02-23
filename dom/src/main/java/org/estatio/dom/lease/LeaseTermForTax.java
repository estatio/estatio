package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Optional;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForTax extends LeaseTerm {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private Boolean taxable;

    public Boolean getTaxable() {
        return taxable;
    }

    public void setTaxable(final Boolean taxable) {
        this.taxable = taxable;
    }

    // //////////////////////////////////////

    private BigDecimal taxableValue;

    @Optional
    @Disabled
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(final BigDecimal taxableValue) {
        this.taxableValue = taxableValue;
    }

    private BigDecimal taxValue;

    @Optional
    @Disabled
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(final BigDecimal taxValue) {
        this.taxValue = taxValue;
    }

    private BigDecimal taxPercentage;

    @javax.jdo.annotations.Column(scale = 1)
    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(final BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

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

    // //////////////////////////////////////

    private String officeName;

    @Optional
    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(final String officeName) {
        this.officeName = officeName;
    }

    private String officeCode;

    @Optional
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(final String officeCode) {
        this.officeCode = officeCode;
    }

    @Persistent
    private LocalDate registrationDate;

    @Optional
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(final LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    private String registrationNumber;

    @Optional
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(final String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

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

    @Override
    public BigDecimal getEffectiveValue() {
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
        }
    }

    @Override
    protected void doAlign() {
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        if (rentItem != null) {
            setTaxableValue(rentItem.valueForDate(getStartDate()));
        }
        // TODO: Disabled the calculation of tax. To be discussed with the users

        // if (getTaxableValue() != null && getTaxValue() == null) {
        // BigDecimal taxFactor = getTaxPercentage().divide(HUNDRED);
        // BigDecimal recoverableFactor =
        // getRecoverablePercentage().divide(HUNDRED);
        // BigDecimal taxValue =
        // getTaxableValue().multiply(taxFactor).multiply(recoverableFactor);
        // setTaxValue(taxValue);
        // }
    }
}
