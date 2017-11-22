package org.estatio.module.budgetassignment.contributions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLink;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

/**
 * This cannot be inlined because Lease doesn't know about BudgetCalculationResultLinkRepository.
 */
@Mixin
public class LeaseTerm_BudgetCalculationResults {

    private final LeaseTermForServiceCharge term;
    public LeaseTerm_BudgetCalculationResults(LeaseTermForServiceCharge term){
        this.term = term;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculationResult> budgetCalculationResults() {
        List<BudgetCalculationResult> results = new ArrayList<>();
        for (BudgetCalculationResultLink link : budgetCalculationResultLinkRepository.findByLeaseTerm(term)){
            results.add(link.getBudgetCalculationResult());
        }
        return results;
    }

    @Inject
    private BudgetCalculationResultLinkRepository budgetCalculationResultLinkRepository;

}
