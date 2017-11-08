package org.estatio.module.budgetassignment.dom.override;

import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculationType;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.budgetassignment.override.BudgetOverrideDummy")
public class BudgetOverrideDummy extends BudgetOverride {
    @Override BudgetOverrideValue valueFor(final LocalDate date, final BudgetCalculationType type) {
        return null;
    }

    @Override public ApplicationTenancy getApplicationTenancy() {
        return null;
    }
}
