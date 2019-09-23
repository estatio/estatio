package org.estatio.module.budget.contributions;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.asset.dom.Property;

import javax.inject.Inject;

@Mixin(method = "act")
public class Property_newBudget {
    private final Property property;

    public Property_newBudget(Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "budgets", sequence = "1")
    public Budget act(
            final int year) {
        return ccoNewBudgetContribution.newBudget(this.property, year);
    }

    public String validateAct(
            final int year) {
        return ccoNewBudgetContribution.validateNewBudget(this.property, year);
    }

    @Inject
    PropertyService ccoNewBudgetContribution;
}
