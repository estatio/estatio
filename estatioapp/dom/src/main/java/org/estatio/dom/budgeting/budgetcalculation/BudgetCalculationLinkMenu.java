package org.estatio.dom.budgeting.budgetcalculation;

import org.apache.isis.applib.annotation.*;

import javax.inject.Inject;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetCalculationLinkMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<BudgetCalculationLink> allBudgetCalculationLinks(){
        return budgetCalculationLinkRepository.allBudgetCalculationLinks();
    }

    @Inject
    private BudgetCalculationLinkRepository budgetCalculationLinkRepository;

}
