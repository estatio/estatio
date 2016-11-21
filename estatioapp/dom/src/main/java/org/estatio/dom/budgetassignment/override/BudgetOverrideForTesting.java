package org.estatio.dom.budgetassignment.override;

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;

public class BudgetOverrideForTesting extends BudgetOverride {
    @Override BudgetOverrideValue resultFor(final LocalDate date, final BudgetCalculationType type) {
        return null;
    }

    @Override public ApplicationTenancy getApplicationTenancy() {
        return null;
    }
}
