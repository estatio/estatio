package org.estatio.app.menus.budget;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.app.services.budget.BudgetImportExportManager;
import org.estatio.dom.asset.Property;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideValue;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideValueRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.budget.BudgetMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Service Charges",
        menuOrder = "60.1"
)
public class BudgetMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Budget> allBudgets() {
        return budgetRepository.allBudgets();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
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

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public Budget findBudget(
            final Property property,
            final Budget budget) {
        return budget;
    }

    public List<Budget> choices1FindBudget(
            final Property property,
            final Budget budget) {
        return budgetRepository.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetOverrideValue> allBudgetOverrideValues(){
        return budgetOverrideValueRepository.allBudgetOverrideValues();
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculationResult> allBudgetCalculationResults(){
        return budgetCalculationResultRepository.allBudgetCalculationResults();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public BudgetImportExportManager uploadBudget() {
        return new BudgetImportExportManager();
    }

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetOverrideValueRepository budgetOverrideValueRepository;

    @Inject BudgetCalculationResultRepository budgetCalculationResultRepository;

}
