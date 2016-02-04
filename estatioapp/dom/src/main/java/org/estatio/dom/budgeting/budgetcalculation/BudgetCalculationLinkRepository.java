package org.estatio.dom.budgeting.budgetcalculation;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

import java.util.List;

@DomainService(repositoryFor = BudgetCalculationLink.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationLinkRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationLink> {

    public BudgetCalculationLinkRepository() {
        super(BudgetCalculationLinkRepository.class, BudgetCalculationLink.class);
    }

    public BudgetCalculationLink findOrCreateBudgetCalculationLink(
            final BudgetCalculation calculation,
            final LeaseTermForServiceCharge term) {

        BudgetCalculationLink budgetCalculationLink = findByBudgetCalculationAndLeaseTerm(calculation, term);

        return budgetCalculationLink == null ? createBudgetCalculationLink(calculation,term) : budgetCalculationLink;

    }

    public BudgetCalculationLink createBudgetCalculationLink(
            final BudgetCalculation budgetCalculation,
            final LeaseTermForServiceCharge leaseTermForServiceCharge) {

        BudgetCalculationLink budgetCalculationLink = newTransientInstance(BudgetCalculationLink.class);
        budgetCalculationLink.setBudgetCalculation(budgetCalculation);
        budgetCalculationLink.setLeaseTerm(leaseTermForServiceCharge);

        persistIfNotAlready(budgetCalculationLink);

        return budgetCalculationLink;

    }

    public List<BudgetCalculationLink> allBudgetCalculationLinks(){
        return allInstances();
    }

    public List<BudgetCalculationLink> findByLeaseTerm(final LeaseTermForServiceCharge leaseTerm) {
        return allMatches("findByLeaseTerm", "leaseTerm", leaseTerm);
    }

    public BudgetCalculationLink findByBudgetCalculationAndLeaseTerm(final BudgetCalculation budgetCalculation, final LeaseTermForServiceCharge leaseTerm) {
        return uniqueMatch("findByBudgetCalculationAndLeaseTerm", "budgetCalculation", budgetCalculation, "leaseTerm", leaseTerm);
    }


}
