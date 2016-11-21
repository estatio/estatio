package org.estatio.dom.budgetassignment.override;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgetassignment.override.BudgetOverrideForMax")
public class BudgetOverrideForMax extends BudgetOverride {

    @Getter @Setter
    @Column(scale = 2)
    private BigDecimal maxValue;

    @Override BudgetOverrideValue resultFor(final LocalDate date, final BudgetCalculationType type) {
        if (getCalculatedValueByBudget(date, type).compareTo(maxValue) > 0) {
            return createCalculation(maxValue, type);
        }
        return null;
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }

}
