package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Optional;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForTax extends LeaseTerm {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

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
        setTaxableValue(rentItem.valueForDate(getStartDate()));
        if (getTaxableValue() != null) {
            BigDecimal taxFactor = getTaxPercentage().divide(HUNDRED);
            BigDecimal recoverableFactor = getRecoverablePercentage().divide(HUNDRED);
            BigDecimal taxValue = getTaxableValue().multiply(taxFactor).multiply(recoverableFactor);
            setTaxValue(taxValue);
        }
    }
}
