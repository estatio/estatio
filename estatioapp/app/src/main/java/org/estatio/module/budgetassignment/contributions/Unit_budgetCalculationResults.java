package org.estatio.module.budgetassignment.contributions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

@Mixin(method = "coll")
public class Unit_budgetCalculationResults {

    private final Unit unit;

    public Unit_budgetCalculationResults(Unit unit) {
        this.unit = unit;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculationResult> coll() {
        List<BudgetCalculationResult> result = new ArrayList<>();
        for (Occupancy o : occupancyRepository.findByUnit(unit)){
            result.addAll(budgetCalculationResultRepository.findByOccupancy(o));
        }
        return result;
    }

    @Inject OccupancyRepository occupancyRepository;

    @Inject BudgetCalculationResultRepository budgetCalculationResultRepository;

}