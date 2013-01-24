package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    // {{ TurnoverRentPercentage (property)
    private BigDecimal turnoverRentPercentage;

    @MemberOrder(sequence = "10", name = "Turnover Rent")
    @Column(scale=4)
    public BigDecimal getTurnoverRentPercentage() {
        return turnoverRentPercentage;
    }

    public void setTurnoverRentPercentage(final BigDecimal turnoverRentPercentage) {
        this.turnoverRentPercentage = turnoverRentPercentage;
    }
    // }}

    // {{ AuditedTurnover (property)
    private BigDecimal auditedTurnover;

    @MemberOrder(sequence = "11", name = "Turnover Rent")
    @Column(scale=4)
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }
    // }}

    // {{ BudgetedTurnover (property)
    private BigDecimal budgetedTurnover;
    
    @MemberOrder(sequence = "12", name = "Turnover Rent")
    @Column(scale=4)
    public BigDecimal getBudgetedTurnover() {
        return budgetedTurnover;
    }
    
    public void setBudgetedTurnover(final BigDecimal budgetedTurnover) {
        this.budgetedTurnover = budgetedTurnover;
    }
    // }}
    
    // {{ BudgetedValue (property)
    private BigDecimal budgetedValue;
    
    @MemberOrder(sequence = "20", name = "Values")
    @Column(scale=4)
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }
    
    public void setBudgetedValue(final BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }
    // }}

    // {{ AuditedValue (property)
    private BigDecimal auditedValue;

    @MemberOrder(sequence = "21", name = "Values")
    @Column(scale=4)
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }
    // }}

}
