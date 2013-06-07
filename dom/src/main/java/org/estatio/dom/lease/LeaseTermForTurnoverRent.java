package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotPersisted;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForTurnoverRent extends LeaseTerm {

    private String turnoverRentRule;

    @MemberOrder(sequence = "10", name = "Turnover Rent")
    public String getTurnoverRentRule() {
        return turnoverRentRule;
    }

    public void setTurnoverRentRule(final String turnoverRentRule) {
        this.turnoverRentRule = turnoverRentRule;
    }

    public String validateTurnoverRentRule(final String rule) {
        if (rule == null || rule.trim().length() == 0)
            return null;
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(rule);
        return helper.isValid() ? null : "This is not a valid rule";
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal budgetedTurnover;

    @MemberOrder(sequence = "11", name = "Turnover Rent")
    public BigDecimal getBudgetedTurnover() {
        return budgetedTurnover;
    }

    public void setBudgetedTurnover(final BigDecimal budgetedTurnover) {
        this.budgetedTurnover = budgetedTurnover;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal auditedTurnover;

    @MemberOrder(sequence = "12", name = "Turnover Rent")
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(final BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    // //////////////////////////////////////

    @NotPersisted
    @MemberOrder(sequence = "13", name = "Turnover Rent")
    public BigDecimal getTurnoverRent() {
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
        return helper.calculateRent(getAuditedTurnover());
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal contractualRent;

    @MemberOrder(sequence = "14", name = "Turnover Rent")
    public BigDecimal getContractualRent() {
        return contractualRent;
    }

    public void setContractualRent(final BigDecimal contractualRent) {
        this.contractualRent = contractualRent;
    }

    // //////////////////////////////////////

    @Override
    protected void update() {
        LeaseItem rentItem = getLeaseItem().getLease().findFirstItemOfType(LeaseItemType.RENT);
        // TODO: Should not be hardcoded searching for rent and should return a
        // collection. Also move the value of rent to a different field
        if (rentItem != null) {
            BigDecimal contractualRent = rentItem.valueForPeriod(getLeaseItem().getInvoicingFrequency(), getStartDate(), getStartDate().plusYears(2));
            setContractualRent(contractualRent);
            setValue(BigDecimal.ZERO);
            BigDecimal turnoverRent = getTurnoverRent();
            if (turnoverRent.compareTo(contractualRent) > 0) {
                setValue(turnoverRent.subtract(contractualRent));
            }
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        LeaseTermForTurnoverRent prev = (LeaseTermForTurnoverRent) getPreviousTerm();
        if (prev != null) {
            setTurnoverRentRule(prev.getTurnoverRentRule());
        }
    }

}
