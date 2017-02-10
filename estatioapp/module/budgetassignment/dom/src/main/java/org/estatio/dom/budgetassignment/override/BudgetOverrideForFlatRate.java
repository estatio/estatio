package org.estatio.dom.budgetassignment.override;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgetassignment.override.BudgetOverrideForFlatRate")
public class BudgetOverrideForFlatRate extends BudgetOverride {

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal valuePerM2;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal weightedArea;

    @Override BudgetOverrideValue valueFor(final LocalDate date, final BudgetCalculationType type) {
        return findOrCreateCalculation(valuePerM2.multiply(weightedArea), type);
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }
}
