package org.estatio.dom.budgetassignment;

import java.math.BigDecimal;

import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;

import lombok.Getter;
import lombok.Setter;

public class ShortFall {

    ShortFall(){
        budgetedShortFall = BigDecimal.ZERO;
        auditedShortFall = BigDecimal.ZERO;
    }

    @Getter @Setter
    private BigDecimal budgetedShortFall;

    @Getter @Setter
    private BigDecimal auditedShortFall;

    public ShortFall add(final ShortFall shortFall) {
        budgetedShortFall = budgetedShortFall.add(shortFall.getBudgetedShortFall());
        auditedShortFall = auditedShortFall.add(shortFall.getAuditedShortFall());
        return this;
    }

    public ShortFall add(final BigDecimal shortFallAmount, final BudgetCalculationType calculationType) {
        if (calculationType == BudgetCalculationType.BUDGETED) {
            budgetedShortFall = budgetedShortFall.add(shortFallAmount);
        }
        if (calculationType == BudgetCalculationType.AUDITED) {
            auditedShortFall = auditedShortFall.add(shortFallAmount);
        }
        return this;
    }

    public BigDecimal getShortFall(final BudgetCalculationType calculationType) {
        if (calculationType == BudgetCalculationType.BUDGETED) {
            return getBudgetedShortFall();
        }
        if (calculationType == BudgetCalculationType.AUDITED) {
            return getAuditedShortFall();
        }
        return null;
    }
}
