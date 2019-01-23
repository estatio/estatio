package org.estatio.module.budgetassignment.dom.service;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.isisaddons.module.excel.dom.PivotColumn;
import org.isisaddons.module.excel.dom.PivotDecoration;
import org.isisaddons.module.excel.dom.PivotRow;
import org.isisaddons.module.excel.dom.PivotValue;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.charge.dom.Charge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.budgetassignment.viewmodels.CalculationResultsForLeaseViewmodel",
        auditing = Auditing.DISABLED
)
public class CalculationResultsForLeaseViewmodel {

    public CalculationResultsForLeaseViewmodel(){}

    public CalculationResultsForLeaseViewmodel(
            final Unit unit,
            final String incomingCharge,
            final BigDecimal valueForLease,
            final BigDecimal totalValueInBudget,
            final Charge invoiceCharge
    ){
        this.unit = unit.getReference();
        this.incomingCharge = incomingCharge;
        this.valueForLease = valueForLease;
        this.totalValueInBudget = totalValueInBudget;
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
    @MemberOrder(sequence = "6")
    @PivotDecoration(order = 1)
    private BigDecimal totalValueInBudget;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    @PivotColumn(order = 1)
    private String invoiceCharge;

}
