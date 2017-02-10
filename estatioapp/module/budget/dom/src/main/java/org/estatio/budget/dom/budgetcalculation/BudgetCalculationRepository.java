package org.estatio.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.budget.dom.partioning.PartitionItem;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.asset.dom.Unit;
import org.estatio.budget.dom.budget.Budget;
import org.estatio.budget.dom.budgetitem.BudgetItem;
import org.estatio.budget.dom.keyitem.KeyItem;
import org.estatio.charge.dom.Charge;

@DomainService(repositoryFor = BudgetCalculation.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationRepository extends UdoDomainRepositoryAndFactory<BudgetCalculation> {

    public BudgetCalculationRepository() {
        super(BudgetCalculationRepository.class, BudgetCalculation.class);
    }

    public BudgetCalculation createBudgetCalculation(
            final PartitionItem partitionItem,
            final KeyItem keyItem,
            final BigDecimal value,
            final BudgetCalculationType calculationType){

        BudgetCalculation budgetCalculation = newTransientInstance(BudgetCalculation.class);
        budgetCalculation.setPartitionItem(partitionItem);
        budgetCalculation.setKeyItem(keyItem);
        budgetCalculation.setValue(value);
        budgetCalculation.setCalculationType(calculationType);
        budgetCalculation.setBudget(partitionItem.getBudget());
        budgetCalculation.setInvoiceCharge(partitionItem.getCharge());
        budgetCalculation.setIncomingCharge(partitionItem.getBudgetItem().getCharge());
        budgetCalculation.setUnit(keyItem.getUnit());

        persist(budgetCalculation);

        return budgetCalculation;
    }

    public BudgetCalculation findOrCreateBudgetCalculation(
            final PartitionItem partitionItem,
            final KeyItem keyItem,
            final BigDecimal value,
            final BudgetCalculationType calculationType) {
        return findUnique(partitionItem, keyItem, calculationType)==null ?
                createBudgetCalculation(partitionItem, keyItem, value, calculationType) :
                findUnique(partitionItem, keyItem, calculationType);
    }

    public BudgetCalculation findUnique(
            final PartitionItem partitionItem,
            final KeyItem keyItem,
            final BudgetCalculationType calculationType
            ){
        return uniqueMatch(
                "findUnique",
                "partitionItem", partitionItem,
                "keyItem", keyItem,
                "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByBudgetAndStatus(Budget budget, Status status) {
        return allMatches("findByBudgetAndStatus", "budget", budget, "status", status);
    }

    public List<BudgetCalculation> findByPartitionItemAndCalculationType(PartitionItem partitionItem, BudgetCalculationType calculationType) {
        return allMatches("findByPartitionItemAndCalculationType", "partitionItem", partitionItem, "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByPartitionItem(
            final PartitionItem partitionItem
    ){
        return allMatches("findByPartitionItem", "partitionItem", partitionItem);
    }

    public List<BudgetCalculation> allBudgetCalculations() {
        return allInstances();
    }

    public List<BudgetCalculation> findByBudgetAndCharge(final Budget budget, final Charge charge) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()){
            for (PartitionItem allocation : budgetItem.getPartitionItems()){
                if (allocation.getCharge().equals(charge)) {
                    result.addAll(findByPartitionItem(allocation));
                }
            }
        }
        return result;
    }

    public List<BudgetCalculation> findByBudget(final Budget budget) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndCalculationType(item, BudgetCalculationType.ACTUAL));
            result.addAll(findByBudgetItemAndCalculationType(item, BudgetCalculationType.BUDGETED));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndCalculationType(final Budget budget, final BudgetCalculationType calculationType) {
        List<BudgetCalculation> result = new ArrayList<>();
        for (BudgetItem item : budget.getItems()){

            result.addAll(findByBudgetItemAndCalculationType(item, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetItemAndCalculationType(final BudgetItem budgetItem, final BudgetCalculationType calculationType) {

        List<BudgetCalculation> result = new ArrayList<>();
        for (PartitionItem allocation : budgetItem.getPartitionItems()) {

            result.addAll(findByPartitionItemAndCalculationType(allocation, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndUnitAndInvoiceChargeAndType(final Budget budget, final Unit unit, final Charge invoiceCharge, final BudgetCalculationType type) {
        return allMatches("findByBudgetAndUnitAndInvoiceChargeAndType", "budget", budget, "unit", unit, "invoiceCharge", invoiceCharge, "type", type);
    }

    public List<BudgetCalculation> findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType(final Budget budget, final Unit unit, final Charge invoiceCharge, final Charge incomingCharge, final BudgetCalculationType type) {
        return allMatches("findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType", "budget", budget, "unit", unit, "invoiceCharge", invoiceCharge, "incomingCharge", incomingCharge, "type", type);
    }

}

