package org.estatio.app.mixins.budgetassignment;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.budgetassignment.dom.override.BudgetOverrideForFixed;
import org.estatio.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;

@Mixin
public class Lease_NewFixedOverride {

    private final Lease lease;
    public Lease_NewFixedOverride(Lease lease){
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public BudgetOverrideForFixed newFixedValue(
            final BigDecimal fixedValue,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate,
            final Charge invoiceCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Charge incomingCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BudgetCalculationType type,
            final String reason
    ) {
        return budgetOverrideRepository.newBudgetOverrideForFixed(fixedValue, lease,startDate,endDate,invoiceCharge,incomingCharge,type, reason);
    }

    public String validateNewFixedValue(
            final BigDecimal fixedValue,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final String reason
    ){
        return budgetOverrideRepository.validateNewBudgetOverride(lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
    }

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

}
