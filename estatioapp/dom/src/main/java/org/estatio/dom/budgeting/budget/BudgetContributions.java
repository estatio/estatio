package org.estatio.dom.budgeting.budget;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.asset.Property;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
@DomainServiceLayout()
public class BudgetContributions {

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Budget> budgets(Property property) {
        return budgetRepository.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "budgets", sequence = "1")
    public Budget newBudget(
            final Property property,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return budgetRepository.newBudget(property, startDate, endDate);
    }

    public String validateNewBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {
        return budgetRepository.validateNewBudget(property, startDate, endDate);
    }

    @Inject
    private BudgetRepository budgetRepository;

}
