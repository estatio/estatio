package org.estatio.dom.budgetassignment;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.dom.budgetassignment.viewmodels.BudgetOverview;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class Budget_budgetOverviewContribution {

    @Action(semantics = SemanticsOf.SAFE, publishing = Publishing.DISABLED)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public BudgetOverview budgetOverview(final Budget budget) {
        final BudgetOverview budgetOverview = new BudgetOverview(budget);
        serviceRegistry2.injectServicesInto(budgetOverview);
        return budgetOverview.init();
    }


    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetAssignmentService budgetAssignmentService;

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private BudgetCalculationLinkRepository budgetCalculationLinkRepository;

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private ServiceChargeItemRepository serviceChargeItemRepository;

    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
