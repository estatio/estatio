package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@MemberGroups({ "General", "Dates", "Turnover Rent", "Related" })
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

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal turnoverRentValue;

    @MemberOrder(sequence = "15", name = "Turnover Rent")
    public BigDecimal getTurnoverRentValue() {
        return turnoverRentValue;
    }

    public void setTurnoverRentValue(final BigDecimal turnoverRentValue) {
        this.turnoverRentValue = turnoverRentValue;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getApprovedValue() {
        if (getStatus().isLocked())
            return getTurnoverRentValue();
        return null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        TurnoverRentRuleHelper helper = new TurnoverRentRuleHelper(getTurnoverRentRule());
        BigDecimal calculatedTurnoverRent = helper.calculateRent(getAuditedTurnover());
        if (calculatedTurnoverRent.compareTo(contractualRent) > 0)
            return calculatedTurnoverRent.subtract(contractualRent);
        return BigDecimal.ZERO;
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
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        LeaseTermForTurnoverRent prev = (LeaseTermForTurnoverRent) getPrevious();
        if (prev != null) {
            setTurnoverRentRule(prev.getTurnoverRentRule());
        }
    }
    
    // //////////////////////////////////////

    @Override
    public LeaseTerm lock() {
        super.lock();

        // guard against invalid updates when called as bulk action
        if (getStatus().isLocked()) {
            return this;
        } 

        setTurnoverRentValue(getTrialValue());
        return this;
    }

}
