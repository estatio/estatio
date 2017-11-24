package org.estatio.module.budgetassignment.contributions;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForMax;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideType;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.lease.dom.Lease;

/**
 * This cannot be inlined because Lease doesn't know about BudgetOverrideRepository.
 */
@Mixin
public class Lease_NewCeilingOverride {

    private final Lease lease;
    public Lease_NewCeilingOverride(Lease lease){
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name="budgetOverrides", sequence = "3")
    public BudgetOverrideForMax newCeiling(
            final BigDecimal maxValue,
            @Nullable
            final LocalDate startDate,
            @Nullable
            final LocalDate endDate,
            final Charge invoiceCharge,
            @Nullable
            final Charge incomingCharge,
            @Nullable
            final BudgetCalculationType type
    ) {
        return budgetOverrideRepository.newBudgetOverrideForMax(maxValue,lease,startDate,endDate,invoiceCharge,incomingCharge,type,BudgetOverrideType.CEILING.reason);
    }

    public List<Charge> choices3NewCeiling() {
        return chargeRepository.allOutgoing();
    }

    public List<Charge> choices4NewCeiling() {
        return chargeRepository.allIncoming();
    }

    public String validateNewCeiling(
            final BigDecimal maxValue,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type
    ){
        return budgetOverrideRepository.validateNewBudgetOverride(lease, startDate, endDate, invoiceCharge, incomingCharge, type, BudgetOverrideType.CEILING.reason);
    }

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    private ChargeRepository chargeRepository;

}
