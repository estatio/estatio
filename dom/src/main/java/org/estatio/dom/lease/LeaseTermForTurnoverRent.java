package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    private String turnoverRentRule;

    @MemberOrder(sequence = "10")
    public String getTurnoverRentRule() {
        return turnoverRentRule;
    }

    public void setTurnoverRentRule(final String turnoverRentRule) {
        this.turnoverRentRule = turnoverRentRule;
    }

    public String validateTurnoverRentRule(final String rule) {
        if (rule == null || rule.trim().length() == 0)
            return null;
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper("rule");
        return helper.isValid() ? null : "This is not a valid rule";
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

    public BigDecimal getTurnoverRent() {
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
        return helper.calculateRent(getAuditedTurnover());
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
            setValue(BigDecimal.ZERO);
            BigDecimal turnoverRent = getTurnoverRent();
            if (turnoverRent.compareTo(valueOfRent) > 0) {
                setValue(turnoverRent.subtract(valueOfRent));
            }
        }
    }

}
