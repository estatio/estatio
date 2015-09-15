package org.estatio.dom.budgeting.budget;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTable;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTables;

/**
 * Created by jodo on 14/09/15.
 */
@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetMenu {


    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Budget> allBudgets() {
        return budgets.allBudgets();
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetKeyTable> allBudgetKeyTables() {
        return budgetKeyTables.allBudgetKeyTables();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Budget newBudget(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate) {
        return budgets.newBudget(property, startDate, endDate);
    }

    public String validateNewBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {
        return budgets.validateNewBudget(property, startDate, endDate);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Budget> findBudgetByProperty(Property property){
        return budgets.findBudgetByProperty(property);
    }

    @Inject
    private Budgets budgets;

    @Inject
    private BudgetKeyTables budgetKeyTables;

}
