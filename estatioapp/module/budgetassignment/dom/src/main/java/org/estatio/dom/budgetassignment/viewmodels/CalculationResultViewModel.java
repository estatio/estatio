package org.estatio.dom.budgetassignment.viewmodels;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.isisaddons.module.excel.dom.PivotColumn;
import org.isisaddons.module.excel.dom.PivotDecoration;
import org.isisaddons.module.excel.dom.PivotRow;
import org.isisaddons.module.excel.dom.PivotValue;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.budgetassignment.viewmodels.CalculationResultViewModel",
        auditing = Auditing.DISABLED
)
public class CalculationResultViewModel {

    public CalculationResultViewModel(){}

    public CalculationResultViewModel(
            final Lease lease,
            final Charge invoiceCharge,
            final BigDecimal budgetedValue,
            final BigDecimal effectiveBudgetedValue,
            final BigDecimal shortfallBudgeted,
            final BigDecimal actualValue,
            final BigDecimal effectiveActualValue,
            final BigDecimal shortfallActual

    ){
        this.leaseReference = lease.getReference();
        this.invoiceCharge = invoiceCharge.getReference();
        this.budgetedValue = budgetedValue;
        this.effectiveBudgetedValue = effectiveBudgetedValue;
        this.shortfallBudgeted = shortfallBudgeted;
        this.actualValue = actualValue;
        this.effectiveActualValue = effectiveActualValue;
        this.shortfallActual = shortfallActual;
    }

    @Getter @Setter
    @PivotRow
    private String leaseReference;

    @Getter @Setter
    @PivotDecoration(order = 1)
    private String unit;

    @Getter @Setter
    @PivotColumn(order = 1)
    private String invoiceCharge;

    @Getter @Setter
    @PivotValue(order = 1)
    private BigDecimal budgetedValue;

    @Getter @Setter
    @PivotValue(order = 2)
    private BigDecimal effectiveBudgetedValue;

    @Getter @Setter
    @PivotValue(order = 3)
    private BigDecimal shortfallBudgeted;

    @Getter @Setter
    @PivotValue(order = 4)
    private BigDecimal actualValue;

    @Getter @Setter
    @PivotValue(order = 5)
    private BigDecimal effectiveActualValue;

    @Getter @Setter
    @PivotValue(order = 6)
    private BigDecimal shortfallActual;

}
