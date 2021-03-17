package org.estatio.module.budgetassignment.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@Mixin(method = "coll")
public class Occupancy_budgetCalculationResults {

    private final Occupancy occupancy;

    public Occupancy_budgetCalculationResults(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculationResult> coll() {
        return budgetCalculationResultRepository.findByOccupancy(occupancy);
    }

    @Inject BudgetCalculationResultRepository budgetCalculationResultRepository;

}