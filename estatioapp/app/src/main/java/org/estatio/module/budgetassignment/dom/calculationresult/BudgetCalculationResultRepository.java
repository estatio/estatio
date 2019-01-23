package org.estatio.module.budgetassignment.dom.calculationresult;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(repositoryFor = BudgetCalculationResult.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationResultRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationResult> {

    public BudgetCalculationResultRepository() {
        super(BudgetCalculationResultRepository.class, BudgetCalculationResult.class);
    }

    public BudgetCalculationResult createBudgetCalculationResult(
            final Budget budget,
            final Occupancy occupancy,
            final Charge invoiceCharge,
            final BudgetCalculationType type,
            final BigDecimal value){

        BudgetCalculationResult budgetCalculationResult = new BudgetCalculationResult(budget, occupancy, invoiceCharge, type, value);
        serviceRegistry2.injectServicesInto(budgetCalculationResult);
        repositoryService.persist(budgetCalculationResult);
        return budgetCalculationResult;
    }

    public BudgetCalculationResult upsertBudgetCalculationResult(
            final Budget budget,
            final Occupancy occupancy,
            final Charge invoiceCharge,
            final BudgetCalculationType type,
            final BigDecimal value){
        BudgetCalculationResult result = findUnique(budget, occupancy, invoiceCharge, type);
        if (result==null) {
            return createBudgetCalculationResult(budget, occupancy, invoiceCharge, type, value);
        } else {
            result.setValue(value);
            return result;
        }
    }

    public BudgetCalculationResult findUnique(
            final Budget budget,
            final Occupancy occupancy,
            final Charge invoiceCharge,
            final BudgetCalculationType type) {
        return uniqueMatch("findUnique", "budget", budget, "occupancy", occupancy, "invoiceCharge", invoiceCharge, "type", type);
    }

    public List<BudgetCalculationResult> findByLeaseTerm(final LeaseTermForServiceCharge leaseTerm){
        return allMatches("findByLeaseTerm", "leaseTerm", leaseTerm);
    }

    public List<BudgetCalculationResult> findByBudget(final Budget budget) {
        return allMatches("findByBudget", "budget", budget);
    }

    public List<BudgetCalculationResult> findByLeaseTermAndBudgetAndType(final LeaseTermForServiceCharge term, final Budget budget, final BudgetCalculationType type) {
        return allMatches("findByLeaseTermAndBudgetAndType", "leaseTerm", term, "budget", budget, "type", type);
    }

    public List<BudgetCalculationResult> allBudgetCalculationResults(){
        return allInstances();
    }

    @Inject private RepositoryService repositoryService;

    @Inject private ServiceRegistry2 serviceRegistry2;
}

