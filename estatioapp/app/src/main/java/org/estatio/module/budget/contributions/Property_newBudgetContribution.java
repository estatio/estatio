package org.estatio.module.budget.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;
import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;

@Mixin(method="coll")
@DomainServiceLayout()
public class Property_newBudgetContribution {

    private final Property property;

    public Property_newBudgetContribution(Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<Budget> coll() {
        return budgetRepository.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "budgets", sequence = "1")
    public Budget newBudget(
            final Property property,
            final int year) {
        Budget budget = budgetRepository.newBudget(property, new LocalDate(year, 01, 01), new LocalDate(year, 12, 31));
        budget.findOrCreatePartitioningForBudgeting();
        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final int year) {
        return budgetRepository.validateNewBudget(property, year);
    }

    @Inject
    private BudgetRepository budgetRepository;

}
