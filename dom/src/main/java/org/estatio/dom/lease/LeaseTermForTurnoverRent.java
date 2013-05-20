package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.utils.MathUtils;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    })*/
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal turnoverRentPercentage;

    @MemberOrder(sequence = "10", name = "Turnover Rent")
    public BigDecimal getTurnoverRentPercentage() {
        return turnoverRentPercentage;
    }

    public void setTurnoverRentPercentage(final BigDecimal turnoverRentPercentage) {
        this.turnoverRentPercentage = turnoverRentPercentage;
    }

    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal budgetedTurnover;

    @MemberOrder(sequence = "11", name = "Turnover Rent")
    public BigDecimal getBudgetedTurnover() {
        return budgetedTurnover;
    }

    public void setBudgetedTurnover(final BigDecimal budgetedTurnover) {
        this.budgetedTurnover = budgetedTurnover;
    }

    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal auditedTurnover;

    @MemberOrder(sequence = "12", name = "Turnover Rent")
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    private BigDecimal budgetedValue;

    @MemberOrder(sequence = "20", name = "Values")
    @Column(scale = 2)
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(final BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal auditedValue;

    @MemberOrder(sequence = "21", name = "Values")
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }

    public void update() {
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        // TODO: Should not be hardcoded searching for rent and should return a
        // collection. Also move the value of rent to a different field
        if (rentItem != null) {
            BigDecimal valueOfRent = rentItem.valueForPeriod(getLeaseItem().getInvoicingFrequency(), getStartDate(), getStartDate().plusYears(2));
            setAuditedValue(valueOfRent);
            if (MathUtils.isZeroOrNull(getTurnoverRentPercentage()) || MathUtils.isZeroOrNull(getAuditedTurnover())) {
                setValue(BigDecimal.ZERO);
            } else {
                BigDecimal turnoverRent = getAuditedTurnover().multiply(getTurnoverRentPercentage()).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                if (turnoverRent.compareTo(valueOfRent) > 0) {
                    setValue(turnoverRent.subtract(valueOfRent));
                }
            }
        }
    }

}
