package org.estatio.app.budget;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.budget.BudgetItem;
import org.estatio.dom.budget.BudgetKeyItem;
import org.estatio.dom.budget.BudgetKeyTable;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationServices {

    public BigDecimal calculatedValuePerBudgetKeyItem(final BudgetItem budgetItem, final BudgetKeyItem budgetKeyItem) {

        BigDecimal calculatedValue = BigDecimal.ZERO;
        BudgetKeyTable budgetKeyTable = budgetKeyItem.getBudgetKeyTable();

        calculatedValue = calculatedValue.add(
                budgetItem.getValue()
                        .multiply(budgetKeyItem.getValue(), MathContext.DECIMAL64)
                        .divide(
                                budgetKeyTable
                                        .getKeyValueMethod()
                                        .keySum(budgetKeyTable) ,
                                MathContext.DECIMAL64
                        )
        );

        return calculatedValue;

    }

}
