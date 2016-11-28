package org.estatio.dom.budgetassignment.viewmodels;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.isisaddons.module.excel.dom.PivotDecoration;
import org.isisaddons.module.excel.dom.PivotRow;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, auditing = Auditing.DISABLED)
public class BudgetCalculationResultViewModel {

    public BudgetCalculationResultViewModel(){}

    public BudgetCalculationResultViewModel(
            final Lease lease,
            final Unit unit,
            final KeyTable keyTable,
            final Charge invoiceCharge,
            final BigDecimal budgetedValue
    ){
        this.leaseReference = lease.getReference();
        this.invoiceCharge = invoiceCharge.getReference();
        this.budgetedValue = budgetedValue;
        this.unit = unit.getReference();
        this.keyTable = keyTable.getName();
    }

    @Getter @Setter
    @PivotRow
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String unit;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    @PivotDecoration(order = 1)
    private String keyTable;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String invoiceCharge;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private BigDecimal budgetedValue;

    public void add(final BudgetCalculationViewmodel calculationResult) {
        if (calculationResult.getCalculationType() == BudgetCalculationType.BUDGETED){
            setBudgetedValue(getBudgetedValue().add(calculationResult.getValue()));
        }
    }
}
