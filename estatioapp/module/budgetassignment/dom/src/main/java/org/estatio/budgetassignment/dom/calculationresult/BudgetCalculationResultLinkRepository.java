package org.estatio.budgetassignment.dom.calculationresult;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

@DomainService(repositoryFor = BudgetCalculationResultLink.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationResultLinkRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationResultLink> {

    public BudgetCalculationResultLinkRepository() {
        super(BudgetCalculationResultLinkRepository.class, BudgetCalculationResultLink.class);
    }

    public BudgetCalculationResultLink createBudgetCalculationResultLink(
            final BudgetCalculationResult budgetCalculationResult,
            final LeaseTermForServiceCharge leaseTermForServiceCharge){

        BudgetCalculationResultLink budgetCalculationResultLink = newTransientInstance(BudgetCalculationResultLink.class);
        budgetCalculationResultLink.setBudgetCalculationResult(budgetCalculationResult);
        budgetCalculationResultLink.setLeaseTermForServiceCharge(leaseTermForServiceCharge);

        persist(budgetCalculationResultLink);

        return budgetCalculationResultLink;
    }

    public BudgetCalculationResultLink findOrCreateLink(
            final BudgetCalculationResult budgetCalculationResult,
            final LeaseTermForServiceCharge leaseTermForServiceCharge) {
        return findUnique(budgetCalculationResult, leaseTermForServiceCharge)  == null ?
                createBudgetCalculationResultLink(budgetCalculationResult, leaseTermForServiceCharge) :
                findUnique(budgetCalculationResult, leaseTermForServiceCharge);
    }

    public BudgetCalculationResultLink findUnique(
            final BudgetCalculationResult budgetCalculationResult,
            final LeaseTermForServiceCharge leaseTermForServiceCharge) {
        return uniqueMatch("findUnique", "budgetCalculationResult", budgetCalculationResult, "leaseTermForServiceCharge", leaseTermForServiceCharge);
    }

    public List<BudgetCalculationResultLink> findByCalculationResult(
            final BudgetCalculationResult budgetCalculationResult) {
        return allMatches("findByCalculationResult", "budgetCalculationResult", budgetCalculationResult);
    }

    public List<BudgetCalculationResultLink> findByLeaseTerm(
            final LeaseTermForServiceCharge leaseTermForServiceCharge) {
        return allMatches("findByLeaseTerm", "leaseTermForServiceCharge", leaseTermForServiceCharge);
    }

    public List<BudgetCalculationResultLink> allBudgetCalculationResultLinks(){
        return allInstances();
    }

}

