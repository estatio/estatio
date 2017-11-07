package org.estatio.dom.budgetassignment.calculationresult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

/**
 * TODO: this could be inlined
 */
@Mixin
public class BudgetCalculationResult_LeaseTerms {

    private final BudgetCalculationResult result;
    public BudgetCalculationResult_LeaseTerms(BudgetCalculationResult result){
        this.result = result;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<LeaseTermForServiceCharge> leaseTerms() {
        List<LeaseTermForServiceCharge> results = new ArrayList<>();
        for (BudgetCalculationResultLink link : budgetCalculationResultLinkRepository.findByCalculationResult(result)){
            results.add(link.getLeaseTermForServiceCharge());
        }
        return results;
    }

    @Inject
    private BudgetCalculationResultLinkRepository budgetCalculationResultLinkRepository;

}
