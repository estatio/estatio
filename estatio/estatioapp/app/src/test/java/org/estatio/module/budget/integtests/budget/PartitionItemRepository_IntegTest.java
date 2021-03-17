package org.estatio.module.budget.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitionItemRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    PartitionItemRepository partitionItemRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PartitioningTableRepository partitioningTableRepository;

    @Before
    public void setupData() {
        runFixtureScript(Partitioning_enum.OxfPartitioning2015.builder());
    }

    public static class FindByPartitionItem extends PartitionItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            BudgetItem budgetItem = budget.getItems().last();
            // when
            final List<PartitionItem> partitionItemList = partitionItemRepository.findByBudgetItem(budgetItem);
            // then
            assertThat(partitionItemList.size()).isEqualTo(2);

        }

    }

    public static class FindUnique extends PartitionItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            BudgetItem budgetItem = budget.getItems().first();
            PartitioningTable partitioningTable = partitioningTableRepository.findByBudget(budget).get(0);
            Charge charge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
            Partitioning partitioning = budget.getPartitionings().first();
            // when
            final PartitionItem partitionItem = partitionItemRepository.findUnique(partitioning, charge, budgetItem, partitioningTable);
            // then
            assertThat(partitionItem.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(partitionItem.getPartitioningTable()).isEqualTo(partitioningTable);
        }

    }

    public static class UpdateOrCreate extends PartitionItemRepository_IntegTest {

        PartitionItem partitionItem;
        BigDecimal origPercentage;
        BigDecimal newPercentage;


        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            BudgetItem budgetItem = budget.getItems().first();
            PartitioningTable partitioningTable = partitioningTableRepository.findByBudget(budget).get(0);
            Charge charge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
            Partitioning partitioning = budget.getPartitionings().first();

            origPercentage = new BigDecimal("100").setScale(6, BigDecimal.ROUND_HALF_UP);
            newPercentage = new BigDecimal("90").setScale(6, BigDecimal.ROUND_HALF_UP);

            partitionItem = partitionItemRepository.findUnique(partitioning, charge, budgetItem, partitioningTable);
            assertThat(partitionItem.getPercentage()).isEqualTo(origPercentage);

            // when
            partitionItem = partitionItemRepository.updateOrCreatePartitionItem(budget.getPartitionings().first(), budgetItem, charge, partitioningTable, newPercentage, null, null);

            // then
            assertThat(partitionItem.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(partitionItem.getPartitioningTable()).isEqualTo(partitioningTable);
            assertThat(partitionItem.getPercentage()).isEqualTo(newPercentage);
        }

    }

}
