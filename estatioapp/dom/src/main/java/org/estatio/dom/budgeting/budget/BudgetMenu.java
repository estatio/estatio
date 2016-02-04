package org.estatio.dom.budgeting.budget;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetMenu {


    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Budget> allBudgets() {
        return budgetRepository.allBudgets();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
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

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public Budget findBudget(Property property, LocalDate date){
        return budgetRepository.findByPropertyAndDate(property, date);
    }

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

}
