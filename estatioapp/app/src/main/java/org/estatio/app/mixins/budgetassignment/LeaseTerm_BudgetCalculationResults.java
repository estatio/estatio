package org.estatio.app.mixins.budgetassignment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationResultLink;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

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
