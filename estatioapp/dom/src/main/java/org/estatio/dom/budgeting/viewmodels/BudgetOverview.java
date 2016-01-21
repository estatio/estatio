package org.estatio.dom.budgeting.viewmodels;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.estatio.app.EstatioViewModel;
import org.estatio.dom.budgeting.BudgetCalculation;
import org.estatio.dom.budgeting.BudgetCalculationContributionServices;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DomainObject(nature = Nature.VIEW_MODEL)
public class BudgetOverview extends EstatioViewModel {

    public BudgetOverview(){}

    public BudgetOverview(final Budget budget) {

        this.budget = budget;
        this.totalBudgetedValue = budget.getTotalBudgetedValue();

    }

    @Getter @Setter
    private Budget budget;

    @Getter @Setter
    private BigDecimal totalBudgetedValue;

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetOverviewLine> getLines(){
        List<BudgetOverviewLine> lines = new ArrayList<>();

        for (Schedule schedule : scheduleRepo.findByBudget(budget)){

            for (BudgetCalculation calculation :  budgetCalculationContributionServices.distributionOverUnits(schedule)) {

                List<Occupancy> occupancyList = occupancies.occupanciesByUnitAndInterval(calculation.getUnit(), budget.getInterval());
                if (occupancyList.size() == 0) {

                    lines.add(new BudgetOverviewLine(null, calculation.getUnit(), schedule.getCharge(), calculation.getValue()));

                } else {

                    for (Occupancy o : occupancyList) {
                        lines.add(new BudgetOverviewLine(o, calculation.getUnit(), schedule.getCharge(), calculation.getValue()));
                    }

                }

            }

        }

        Collections.sort(lines);

        return lines;
    }

    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "Budget overview");
    }


    @Inject
    private Schedules scheduleRepo;

    @Inject
    private BudgetCalculationContributionServices budgetCalculationContributionServices;

    @Inject
    private Occupancies occupancies;

}
