package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

@DomainService(repositoryFor = BudgetCalculation.class, nature = NatureOfService.DOMAIN)
public class BudgetCalculationRepository extends UdoDomainRepositoryAndFactory<BudgetCalculation> {

    public BudgetCalculationRepository() {
        super(BudgetCalculationRepository.class, BudgetCalculation.class);
    }

    public BudgetCalculation createBudgetCalculation(
            final PartitionItem partitionItem,
            final PartitioningTableItem tableItem,
            final BigDecimal value,
            final BudgetCalculationType calculationType){

        BudgetCalculation budgetCalculation = newTransientInstance(BudgetCalculation.class);
        budgetCalculation.setPartitionItem(partitionItem);
        budgetCalculation.setTableItem(tableItem);
        budgetCalculation.setValue(value);
        budgetCalculation.setCalculationType(calculationType);
        budgetCalculation.setBudget(partitionItem.getBudget());
        budgetCalculation.setInvoiceCharge(partitionItem.getCharge());
        budgetCalculation.setIncomingCharge(partitionItem.getBudgetItem().getCharge());
        budgetCalculation.setUnit(tableItem.getUnit());

        persist(budgetCalculation);

        return budgetCalculation;
    }

    public BudgetCalculation findOrCreateBudgetCalculation(
            final PartitionItem partitionItem,
            final PartitioningTableItem keyItem,
            final BigDecimal value,
            final BudgetCalculationType calculationType) {
        return findUnique(partitionItem, keyItem, calculationType)==null ?
                createBudgetCalculation(partitionItem, keyItem, value, calculationType) :
                findUnique(partitionItem, keyItem, calculationType);
    }

    public BudgetCalculation findUnique(
            final PartitionItem partitionItem,
            final PartitioningTableItem tableItem,
            final BudgetCalculationType calculationType
            ){
        return uniqueMatch(
                "findUnique",
                "partitionItem", partitionItem,
                "tableItem", tableItem,
                "calculationType", calculationType);
    }

    public List<BudgetCalculation> findByPartitionItemAndCalculationType(PartitionItem partitionItem, BudgetCalculationType calculationType) {
        return allMatches("findByPartitionItemAndCalculationType", "partitionItem", partitionItem, "calculationType", calculationType);
    }

    public List<BudgetCalculation> allBudgetCalculations() {
        return allInstances();
    }

    public List<BudgetCalculation> findByBudget(final Budget budget) {
        return allMatches("findByBudget", "budget", budget);
    }

    public List<BudgetCalculation> findByBudgetItemAndCalculationType(final BudgetItem budgetItem, final BudgetCalculationType calculationType) {

        List<BudgetCalculation> result = new ArrayList<>();
        for (PartitionItem allocation : budgetItem.getPartitionItems()) {

            result.addAll(findByPartitionItemAndCalculationType(allocation, calculationType));

        }
        return result;
    }

    public List<BudgetCalculation> findByBudgetAndTypeAndStatus(final Budget budget, final BudgetCalculationType type, final Status status){
        return allMatches("findByBudgetAndTypeAndStatus", "budget", budget, "type", type, "status", status);
    }

    public List<BudgetCalculation> findByBudgetAndUnitAndType(final Budget budget, final Unit unit, final BudgetCalculationType type) {
        return allMatches("findByBudgetAndUnitAndType", "budget", budget, "unit", unit, "type", type);
    }

    public List<BudgetCalculation> findByBudgetAndUnitAndInvoiceChargeAndType(final Budget budget, final Unit unit, final Charge invoiceCharge, final BudgetCalculationType type) {
        return allMatches("findByBudgetAndUnitAndInvoiceChargeAndType", "budget", budget, "unit", unit, "invoiceCharge", invoiceCharge, "type", type);
    }

    public List<BudgetCalculation> findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType(final Budget budget, final Unit unit, final Charge invoiceCharge, final Charge incomingCharge, final BudgetCalculationType type) {
        return allMatches("findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType", "budget", budget, "unit", unit, "invoiceCharge", invoiceCharge, "incomingCharge", incomingCharge, "type", type);
    }

}

