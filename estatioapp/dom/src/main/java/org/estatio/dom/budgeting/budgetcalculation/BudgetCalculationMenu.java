package org.estatio.dom.budgeting.budgetcalculation;

import org.apache.isis.applib.annotation.*;

import javax.inject.Inject;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetCalculationMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<BudgetCalculation> allBudgetCalculations(){
        return budgetCalculationRepository.allBudgetCalculations();
    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

}
