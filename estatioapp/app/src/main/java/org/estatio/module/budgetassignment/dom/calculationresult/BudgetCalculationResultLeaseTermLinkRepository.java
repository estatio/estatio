package org.estatio.module.budgetassignment.dom.calculationresult;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

@DomainService(repositoryFor = BudgetCalculationResultLeaseTermLink.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationResultLeaseTermLinkRepository extends UdoDomainRepositoryAndFactory<BudgetCalculationResultLeaseTermLink> {

    public BudgetCalculationResultLeaseTermLinkRepository() {
        super(BudgetCalculationResultLeaseTermLinkRepository.class, BudgetCalculationResultLeaseTermLink.class);
    }

    public BudgetCalculationResultLeaseTermLink findOrCreate(final BudgetCalculationResult budgetCalculationResult, final LeaseTermForServiceCharge leaseTerm){
        final BudgetCalculationResultLeaseTermLink existingLink = findUnique(budgetCalculationResult, leaseTerm);
        if (existingLink ==null){
            return create(budgetCalculationResult, leaseTerm);
        } else {
            return existingLink;
        }
    }

    private BudgetCalculationResultLeaseTermLink create(final BudgetCalculationResult budgetCalculationResult, final LeaseTermForServiceCharge leaseTerm){
        BudgetCalculationResultLeaseTermLink link = new BudgetCalculationResultLeaseTermLink(budgetCalculationResult, leaseTerm);
        repositoryService.persistAndFlush(link);
        return link;
    }

    public BudgetCalculationResultLeaseTermLink findUnique(
            final BudgetCalculationResult budgetCalculationResult,
            final LeaseTermForServiceCharge leaseTerm) {
        return uniqueMatch("findUnique", "budgetCalculationResult", budgetCalculationResult, "leaseTerm", leaseTerm);
    }

    public List<BudgetCalculationResultLeaseTermLink> findByLeaseTerm(final LeaseTermForServiceCharge leaseTerm){
        return allMatches("findByLeaseTerm", "leaseTerm", leaseTerm);
    }

    public List<BudgetCalculationResultLeaseTermLink> findByBudgetCalculationResult(final BudgetCalculationResult budgetCalculationResult) {
        return allMatches("findByBudgetCalculationResult", "budgetCalculationResult", budgetCalculationResult);
    }

    public List<BudgetCalculationResultLeaseTermLink> listAll(){
        return allInstances();
    }

    @Inject private RepositoryService repositoryService;
}

