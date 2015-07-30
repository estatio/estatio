package org.estatio.app.budget;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budget.Budget;
import org.estatio.dom.budget.BudgetItem;
import org.estatio.dom.budget.BudgetKeyItem;
import org.estatio.dom.budget.BudgetKeyItems;
import org.estatio.dom.budget.BudgetKeyTable;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.Occupancy;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationServices {

    public BigDecimal calculatedValuePerBudgetKeyItem(final BudgetItem budgetItem, final BudgetKeyItem budgetKeyItem) {

        BigDecimal calculatedValue = BigDecimal.ZERO;
        BudgetKeyTable budgetKeyTable = budgetKeyItem.getBudgetKeyTable();

        calculatedValue = calculatedValue.add(
                budgetItem.getValue()
                        .multiply(budgetKeyItem.getKeyValue())
                        .divide(
                                budgetKeyTable
                                        .getKeyValueMethod()
                                        .keySum(budgetKeyTable) ,
                                MathContext.DECIMAL32
                        )
        );

        return calculatedValue;

    }

    public BigDecimal calculateValueOnCurrentLeaseTermForServiceCharge(final Lease lease, final Budget budget) {

        BigDecimal calculatedValue = BigDecimal.ZERO;
        LeaseItem li = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
        if (li != null) {

            LeaseTermForServiceCharge lt = (LeaseTermForServiceCharge) li.currentTerm(budget.getStartDate());

            for (Occupancy o : lease.getOccupancies()) {

                // for each budgetItem
                for (BudgetItem budgetItem : budget.getBudgetItems()){

                    // only when charge matches
                    if (li.getCharge().equals(budgetItem.getCharge())) {

                        // calculate and add the value for each occupied unit
                        Unit unit = o.getUnit();
                        BudgetKeyItem budgetKeyItem = budgetKeyItems.findByBudgetKeyTableAndUnit(budgetItem.getBudgetKeyTable(), unit);
                        calculatedValue = calculatedValue.add(calculatedValuePerBudgetKeyItem(budgetItem, budgetKeyItem));

                    }

                }

            }

        }
        return calculatedValue;
    }

    @Inject
    BudgetKeyItems budgetKeyItems;

}
