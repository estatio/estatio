package org.estatio.app.menus.budget;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.dom.budgetassignment.BudgetCalculationLink;
import org.estatio.dom.budgetassignment.BudgetCalculationLinkRepository;

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
