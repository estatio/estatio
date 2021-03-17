package org.estatio.module.budget.dom.keytable;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.partioning.PartitionItem;

public class DirectCostTable_Test {

    @Test
    public void calculateInMemFor_works() {

        // given
        Budget budget = new Budget();
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setBudget(budget);

        DirectCostTable directCostTable = new DirectCostTable();
        DirectCost directCost1 = new DirectCost();
        directCost1.setBudgetedCost(new BigDecimal("123.45"));
        directCostTable.getItems().add(directCost1);

        DirectCost directCost2 = new DirectCost();
        directCost2.setBudgetedCost(new BigDecimal("234.56"));
        directCostTable.getItems().add(directCost2);

        PartitionItem partitionItem = new PartitionItem();
        partitionItem.setBudgetItem(budgetItem);

        LocalDate calcStartDate = new LocalDate(2019,1,1);
        LocalDate calcEndDate = new LocalDate(2019,12,31);

        // when
        final List<InMemBudgetCalculation> calculations = directCostTable
                .calculateInMemFor(partitionItem, null, BudgetCalculationType.BUDGETED, calcStartDate, calcEndDate);
        // then
        Assertions.assertThat(calculations).hasSize(2);
        final InMemBudgetCalculation firstCalc = calculations.get(0);
        Assertions.assertThat(firstCalc.getValue()).isEqualTo(new BigDecimal("123.45"));
        Assertions.assertThat(firstCalc.getCalculationStartDate()).isEqualTo(calcStartDate);
        Assertions.assertThat(firstCalc.getCalculationEndDate()).isEqualTo(calcEndDate);
        Assertions.assertThat(firstCalc.getCalculationType()).isEqualTo(BudgetCalculationType.BUDGETED);
        Assertions.assertThat(firstCalc.getBudget()).isEqualTo(budget);
        Assertions.assertThat(firstCalc.getPartitionItem()).isEqualTo(partitionItem);
        Assertions.assertThat(firstCalc.getTableItem()).isEqualTo(directCost1);
        Assertions.assertThat(firstCalc.getUnit()).isNull();
        Assertions.assertThat(firstCalc.getIncomingCharge()).isNull();
        Assertions.assertThat(firstCalc.getInvoiceCharge()).isNull();

        final InMemBudgetCalculation secondCalc = calculations.get(1);
        Assertions.assertThat(secondCalc.getValue()).isEqualTo(new BigDecimal("234.56"));
        Assertions.assertThat(secondCalc.getCalculationStartDate()).isEqualTo(calcStartDate);
        Assertions.assertThat(secondCalc.getCalculationEndDate()).isEqualTo(calcEndDate);
        Assertions.assertThat(secondCalc.getCalculationType()).isEqualTo(BudgetCalculationType.BUDGETED);
        Assertions.assertThat(secondCalc.getBudget()).isEqualTo(budget);
        Assertions.assertThat(secondCalc.getPartitionItem()).isEqualTo(partitionItem);
        Assertions.assertThat(secondCalc.getTableItem()).isEqualTo(directCost2);
        Assertions.assertThat(secondCalc.getUnit()).isNull();
        Assertions.assertThat(secondCalc.getIncomingCharge()).isNull();
        Assertions.assertThat(secondCalc.getInvoiceCharge()).isNull();

    }

    @Test
    public void auditedCostForCalculationPeriod_works() throws Exception {

        LocalDateInterval calculationInterval;

        //given
        LocalDate budgetStartDate = new LocalDate(2020,1,1);
        LocalDate budgetEndDate = new LocalDate(2020,12,31);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budget.setEndDate(budgetEndDate);
        DirectCostTable directCostTable = new DirectCostTable();
        directCostTable.setBudget(budget);
        DirectCost directCost = new DirectCost();
        directCost.setPartitioningTable(directCostTable);

        // when overlap and no audited cost
        calculationInterval = LocalDateInterval.including(budgetStartDate, budgetStartDate);
        directCost.setAuditedCost(null);
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isNull();

        // when audited cost and one day overlap
        directCost.setAuditedCost(new BigDecimal("366.00"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.minusDays(1), budgetStartDate);
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(new BigDecimal("1.000000"));
        // and when
        directCost.setAuditedCost(new BigDecimal("366.01"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.minusDays(1), budgetStartDate);
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(new BigDecimal("1.000027"));

        // when audited cost and 365 day overlap
        directCost.setAuditedCost(new BigDecimal("366.00"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.minusDays(1), budgetEndDate.minusDays(1));
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(new BigDecimal("365.000000"));
        // and when
        directCost.setAuditedCost(new BigDecimal("366.01"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.minusDays(1), budgetEndDate.minusDays(1));
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(new BigDecimal("365.009973"));
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(directCost.getAuditedCost().subtract(new BigDecimal("1.000027")));

        // when no overlap
        directCost.setAuditedCost(new BigDecimal("366.00"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.minusYears(1), budgetStartDate.minusDays(1));
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isNull();

        // when calculation interval contains budget interval
        directCost.setAuditedCost(new BigDecimal("366.00"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.minusYears(1), budgetEndDate.plusYears(1));
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(directCost.getAuditedCost());

        // when budget interval contains calculation interval
        directCost.setAuditedCost(new BigDecimal("366.00"));
        calculationInterval = LocalDateInterval.including(budgetStartDate.plusDays(1), budgetEndDate.minusDays(1));
        // then
        Assertions.assertThat(DirectCostTable.auditedCostForCalculationPeriod(directCost, calculationInterval)).isEqualTo(new BigDecimal("364.000000"));

    }
}