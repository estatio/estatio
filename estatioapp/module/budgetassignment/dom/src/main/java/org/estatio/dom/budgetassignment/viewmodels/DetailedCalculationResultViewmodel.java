package org.estatio.dom.budgetassignment.viewmodels;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.isisaddons.module.excel.dom.PivotColumn;
import org.isisaddons.module.excel.dom.PivotDecoration;
import org.isisaddons.module.excel.dom.PivotRow;
import org.isisaddons.module.excel.dom.PivotValue;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.charge.Charge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.budgetassignment.viewmodels.DetailedCalculationResultViewmodel",
        auditing = Auditing.DISABLED
)
public class DetailedCalculationResultViewmodel {

    public DetailedCalculationResultViewmodel(){}

    public DetailedCalculationResultViewmodel(
            final Unit unit,
            final String incomingCharge,
            final BigDecimal valueForLease,
            final BigDecimal effectiveValueForLease,
            final BigDecimal shortfall,
            final BigDecimal totalValueInBudget,
            final Charge invoiceCharge
    ){
        this.unit = unit.getReference();
        this.incomingCharge = incomingCharge;
        this.valueForLease = valueForLease;
        this.totalValueInBudget = totalValueInBudget;
        this.effectiveValueForLease = effectiveValueForLease;
        this.shortfall = shortfall;
        this.invoiceCharge = invoiceCharge.getReference();
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    @PivotColumn(order = 1)
    private String unit;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    @PivotRow
    private String incomingCharge;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    @PivotValue(order = 2)
    private BigDecimal valueForLease;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    @PivotValue(order = 3)
    private BigDecimal effectiveValueForLease;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    @PivotValue(order = 4)
    private BigDecimal shortfall;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    @PivotDecoration(order = 1)
    private BigDecimal totalValueInBudget;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    @PivotColumn(order = 1)
    private String invoiceCharge;

}
