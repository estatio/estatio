package org.estatio.module.budget.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;
import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PropertyService {

    public List<Budget> budgets(Property property) {
        return budgetRepository.findByProperty(property);
    }

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
