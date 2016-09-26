package org.estatio.dom.budgetassignment.viewmodels;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationResult;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, auditing = Auditing.DISABLED)
public class BudgetAssignmentResult {

    public BudgetAssignmentResult(){}

    public BudgetAssignmentResult(
            final Lease lease,
            final Unit unit,
            final Charge invoiceCharge,
            final BigDecimal budgetedAmount
    ){
        this.leaseReference = lease.getReference();
        this.unit = unit.getReference();
        this.invoiceCharge = invoiceCharge.getReference();
        this.budgetedAmount = budgetedAmount;
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String unit;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String invoiceCharge;

    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "4")
    private BigDecimal budgetedAmount;

    public void add(final BudgetCalculationResult calculationResult) {
        if (calculationResult.getCalculationType() == BudgetCalculationType.BUDGETED){
            setBudgetedAmount(getBudgetedAmount().add(calculationResult.getValue()));
        }
    }
}
