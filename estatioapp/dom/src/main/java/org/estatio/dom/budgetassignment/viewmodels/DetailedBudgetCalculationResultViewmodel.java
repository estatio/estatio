package org.estatio.dom.budgetassignment.viewmodels;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, auditing = Auditing.DISABLED)
public class DetailedBudgetCalculationResultViewmodel {

    public DetailedBudgetCalculationResultViewmodel(){}

    public DetailedBudgetCalculationResultViewmodel(
            final Unit unit,
            final Charge incomingCharge,
            final String incomingChargeAddedInfo,
            final BigDecimal budgetedValue,
            final KeyTable keyTable,
            final Charge invoiceCharge
    ){
        this.unit = unit.getReference();
        this.incomingCharge = incomingCharge.getReference()
                .concat(" ")
                .concat(incomingCharge.getName())
                .concat(incomingChargeAddedInfo);
        this.keyTable = keyTable.getName();
        this.invoiceCharge = invoiceCharge.getReference();
        this.budgetedValue = budgetedValue;
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String unit;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String incomingCharge;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private BigDecimal budgetedValue;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String keyTable;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private String invoiceCharge;

    public void add(final BudgetCalculationViewmodel calculationResult) {
        if (calculationResult.getCalculationType() == BudgetCalculationType.BUDGETED){
            setBudgetedValue(getBudgetedValue().add(calculationResult.getValue()));
        }
    }


}
