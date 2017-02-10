package org.estatio.budgetassignment.dom.calculationresult;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.charge.dom.Charge;

@DomainService(repositoryFor = BudgetCalculationResult.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationResultRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationResult> {

    public BudgetCalculationResultRepository() {
        super(BudgetCalculationResultRepository.class, BudgetCalculationResult.class);
    }

    public BudgetCalculationResult createBudgetCalculationResult(
            final BudgetCalculationRun run,
            final Charge invoiceCharge){

        BudgetCalculationResult budgetCalculationResult = newTransientInstance(BudgetCalculationResult.class);
        budgetCalculationResult.setBudgetCalculationRun(run);
        budgetCalculationResult.setInvoiceCharge(invoiceCharge);

        persist(budgetCalculationResult);

        return budgetCalculationResult;
    }

    public BudgetCalculationResult findOrCreateBudgetCalculationResult(
            final BudgetCalculationRun run,
            final Charge invoiceCharge){
        BudgetCalculationResult result = findUnique(run, invoiceCharge);
        return result==null ? createBudgetCalculationResult(run, invoiceCharge) : result;
    }

    public BudgetCalculationResult findUnique(
            final BudgetCalculationRun run,
            final Charge invoiceCharge) {
        return uniqueMatch("findUnique", "budgetCalculationRun", run, "invoiceCharge", invoiceCharge);
    }

    public List<BudgetCalculationResult> allBudgetCalculationResults(){
        return allInstances();
    }

}

