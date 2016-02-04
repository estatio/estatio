package org.estatio.dom.budgeting.viewmodels;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.estatio.app.EstatioViewModel;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.CalculationType;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
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
        this.totalAuditedValue = budget.getTotalAuditedValue();

    }

    @Getter @Setter
    private Budget budget;

    @Getter @Setter
    private BigDecimal totalBudgetedValue;

    @Getter @Setter
    private BigDecimal totalAuditedValue;

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetOverviewLine> getBudgeted(){
        List<BudgetOverviewLine> lines = new ArrayList<>();

        for (BudgetItem budgetItem : budget.getItems()) {
            for (BudgetItemAllocation budgetItemAllocation : budgetItemAllocationRepository.findByBudgetItem(budgetItem)) {

                for (BudgetCalculation calculation : budgetCalculationRepository.findByBudgetItemAllocationAndCalculationType(budgetItemAllocation, CalculationType.BUDGETED)) {

                    List<Occupancy> occupancyList = occupancies.occupanciesByUnitAndInterval(calculation.getKeyItem().getUnit(), budget.getInterval());
                    if (occupancyList.size() == 0) {

                        lines = aggregateByCharge(new BudgetOverviewLine(null, calculation.getKeyItem().getUnit(), budgetItemAllocation.getCharge(), calculation.getValue()), lines);

                    } else {

                        for (Occupancy o : occupancyList) {
                            lines = aggregateByCharge(new BudgetOverviewLine(o, calculation.getKeyItem().getUnit(), budgetItemAllocation.getCharge(), calculation.getValue()), lines);
                        }

                    }

                }

            }
        }

        Collections.sort(lines);

        return lines;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetOverviewLine> getAudited(){
        List<BudgetOverviewLine> lines = new ArrayList<>();

        for (BudgetItem budgetItem : budget.getItems()) {
            for (BudgetItemAllocation budgetItemAllocation : budgetItemAllocationRepository.findByBudgetItem(budgetItem)) {

                for (BudgetCalculation calculation : budgetCalculationRepository.findByBudgetItemAllocationAndCalculationType(budgetItemAllocation, CalculationType.AUDITED)) {

                    List<Occupancy> occupancyList = occupancies.occupanciesByUnitAndInterval(calculation.getKeyItem().getUnit(), budget.getInterval());
                    if (occupancyList.size() == 0) {

                        lines = aggregateByCharge(new BudgetOverviewLine(null, calculation.getKeyItem().getUnit(), budgetItemAllocation.getCharge(), calculation.getValue()), lines);

                    } else {

                        for (Occupancy o : occupancyList) {
                            lines = aggregateByCharge(new BudgetOverviewLine(o, calculation.getKeyItem().getUnit(), budgetItemAllocation.getCharge(), calculation.getValue()), lines);
                        }

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

    List<BudgetOverviewLine> aggregateByCharge(BudgetOverviewLine lineToBeMerged, List<BudgetOverviewLine> list) {

        for (BudgetOverviewLine line : list){
            if (lineToBeMerged.getOccupancy() != null) {
                if (lineToBeMerged.getOccupancy().equals(line.getOccupancy()) && lineToBeMerged.getCharge().equals(line.getCharge())) {
                    BigDecimal newAmount = line.getAmount().add(lineToBeMerged.getAmount());
                    line.setAmount(newAmount);
                    return list;
                }
            } else {
                if (lineToBeMerged.getUnit().equals(line.getUnit()) && lineToBeMerged.getCharge().equals(line.getCharge())) {
                    BigDecimal newAmount = line.getAmount().add(lineToBeMerged.getAmount());
                    line.setAmount(newAmount);
                    return list;
                }
            }
        }
        // when no merge has taken place
        list.add(lineToBeMerged);
        return list;
    }

    @Inject
    BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private Occupancies occupancies;

}
