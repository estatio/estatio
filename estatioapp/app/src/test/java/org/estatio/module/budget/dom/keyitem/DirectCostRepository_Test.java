package org.estatio.module.budget.dom.keyitem;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.Partitioning;

public class DirectCostRepository_Test {

    @Test
    public void upsertValuesUsingBusinessLogicOrCreate_works_for_upsert() {

        // given
        final BigDecimal originalBudgetedCost = new BigDecimal("1000.00");
        final BigDecimal alteredBudgetedCost = new BigDecimal("1234.56");
        final BigDecimal alteredAuditedCost = new BigDecimal("1111.11");

        Budget budget = new Budget();
        DirectCostTable directCostTable = new DirectCostTable();
        directCostTable.setBudget(budget);
        DirectCost directCost = new DirectCost();
        directCost.setBudgetedCost(originalBudgetedCost);
        directCost.setAuditedCost(null);
        DirectCostRepository repository = new DirectCostRepository(){
            @Override public DirectCost findUnique(
                    final DirectCostTable directCostTable, final Unit unit) {
                return directCost;
            }
        };

        // when
        budget.setStatus(Status.RECONCILED);
        DirectCost result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, alteredBudgetedCost,
                        alteredAuditedCost);
        // then
        Assertions.assertThat(result).isEqualTo(directCost);
        Assertions.assertThat(result.getBudgetedCost()).isEqualTo(originalBudgetedCost);
        Assertions.assertThat(result.getAuditedCost()).isNull();

        // when
        budget.setStatus(Status.ASSIGNED);
        result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, alteredBudgetedCost,
                        alteredAuditedCost);
        // then
        Assertions.assertThat(result).isEqualTo(directCost);
        Assertions.assertThat(result.getBudgetedCost()).isEqualTo(originalBudgetedCost);
        Assertions.assertThat(result.getAuditedCost()).isEqualTo(alteredAuditedCost);

        // reset directCost
        directCost.setBudgetedCost(originalBudgetedCost);
        directCost.setAuditedCost(null);
        // when
        budget.setStatus(Status.NEW);
        result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, alteredBudgetedCost,
                        alteredAuditedCost);
        // then
        Assertions.assertThat(result).isEqualTo(directCost);
        Assertions.assertThat(result.getBudgetedCost()).isEqualTo(alteredBudgetedCost);
        Assertions.assertThat(result.getAuditedCost()).isEqualTo(alteredAuditedCost);

    }

    @Test
    public void upsertValuesUsingBusinessLogicOrCreate_works_for_create() {

        // given
        Budget budget = new Budget();
        DirectCostTable directCostTable = new DirectCostTable();
        directCostTable.setBudget(budget);
        DirectCost directCost = new DirectCost();
        DirectCostRepository repository = new DirectCostRepository(){
            @Override public DirectCost findUnique(
                    final DirectCostTable directCostTable, final Unit unit) {
                return null;
            }

            @Override public DirectCost newDirectCost(
                    final DirectCostTable directCostTable,
                    final Unit unit,
                    final BigDecimal budgetedValue,
                    final BigDecimal auditedValue) {
                return directCost;
            }
        };

        // when
        budget.setStatus(Status.RECONCILED);
        DirectCost result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, null,
                        null);
        // then
        Assertions.assertThat(result).isNull();

        // when
        budget.setStatus(Status.ASSIGNED);
        result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, null,
                        null);
        // then
        Assertions.assertThat(result).isEqualTo(directCost);

        // when
        budget.setStatus(Status.ASSIGNED);
        Partitioning partitioning = new Partitioning();
        partitioning.setType(BudgetCalculationType.BUDGETED);
        PartitionItem partitionItem = new PartitionItem();
        partitionItem.setPartitioningTable(directCostTable);
        partitioning.getItems().add(partitionItem);
        budget.getPartitionings().add(partitioning);
        result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, null,
                        null);
        // then
        Assertions.assertThat(result).isNull();

        // when
        budget.setStatus(Status.NEW);
        result = repository
                .upsertValuesUsingBusinessLogicOrCreate(directCostTable, null, null,
                        null);
        // then
        Assertions.assertThat(result).isEqualTo(directCost);

    }
}