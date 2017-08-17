package org.estatio.app.mixins.budgetassignment;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.lease.Lease;

/**
 * This cannot be inlined because Lease doesn't know about BudgetCalculationRunRepository.
 */
@Mixin
public class Lease_BudgetCalculationRuns {

    private final Lease lease;
    public Lease_BudgetCalculationRuns(Lease lease){
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculationRun> budgetCalculationRuns() {
        return budgetCalculationRunRepository.findByLease(lease);
    }

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

}
