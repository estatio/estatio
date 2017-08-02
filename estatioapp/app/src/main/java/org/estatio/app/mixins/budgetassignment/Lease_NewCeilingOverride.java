package org.estatio.app.mixins.budgetassignment;

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

import org.estatio.dom.budgetassignment.override.BudgetOverrideForMax;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverrideType;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.lease.Lease;

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
