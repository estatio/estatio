package org.estatio.module.budgetassignment.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLinkRepository;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

@Mixin
public class LeaseTermForServiceCharge_controlledByBudget {

    private final LeaseTermForServiceCharge leaseTermForServiceCharge;

    public LeaseTermForServiceCharge_controlledByBudget(LeaseTermForServiceCharge leaseTermForServiceCharge) {
        this.leaseTermForServiceCharge = leaseTermForServiceCharge;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public boolean $$() {
        return !budgetCalculationResultLeaseTermLinkRepository
                .findByLeaseTerm(leaseTermForServiceCharge).isEmpty();
    }

    @Inject
    BudgetCalculationResultLeaseTermLinkRepository budgetCalculationResultLeaseTermLinkRepository;
}
