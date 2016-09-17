package org.estatio.dom.budgetassignment;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;

@DomainService(repositoryFor = BudgetCalculationLink.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationLinkRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationLink> {

    public BudgetCalculationLinkRepository() {
        super(BudgetCalculationLinkRepository.class, BudgetCalculationLink.class);
    }

    public BudgetCalculationLink findOrCreateBudgetCalculationLink(
            final BudgetCalculation calculation,
            final ServiceChargeItem term) {

        BudgetCalculationLink budgetCalculationLink = findByBudgetCalculationAndServiceChargeTerm(calculation, term);

        return budgetCalculationLink == null ? createBudgetCalculationLink(calculation,term) : budgetCalculationLink;

    }

    public BudgetCalculationLink createBudgetCalculationLink(
            final BudgetCalculation budgetCalculation,
            final ServiceChargeItem serviceChargeItem) {

        BudgetCalculationLink budgetCalculationLink = newTransientInstance(BudgetCalculationLink.class);
        budgetCalculationLink.setBudgetCalculation(budgetCalculation);
        budgetCalculationLink.setServiceChargeItem(serviceChargeItem);

        persistIfNotAlready(budgetCalculationLink);

        return budgetCalculationLink;

    }

    public List<BudgetCalculationLink> allBudgetCalculationLinks(){
        return allInstances();
    }

    public BudgetCalculationLink findByBudgetCalculationAndServiceChargeTerm(final BudgetCalculation budgetCalculation, final ServiceChargeItem serviceChargeItem) {
        return uniqueMatch("findByBudgetCalculationAndServiceChargeItem", "budgetCalculation", budgetCalculation, "serviceChargeItem", serviceChargeItem);
    }

    public List<BudgetCalculationLink> findByBudgetCalculation(final BudgetCalculation budgetCalculation) {
        return allMatches("findByBudgetCalculation", "budgetCalculation", budgetCalculation);
    }

    public List<BudgetCalculationLink> findByServiceChargeTerm(final ServiceChargeItem serviceChargeItem) {
        return allMatches("findByServiceChargeItem", "serviceChargeItem", serviceChargeItem);
    }

}
