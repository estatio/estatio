package org.estatio.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.partioning.PartitionItemRepository;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.PartitioningAndItemsForOxf;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitionItemRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartitionItemRepository partitionItemRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    KeyTableRepository keytablesRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PartitioningAndItemsForOxf());
            }
        });
    }

    public static class validateNewPartitionItem extends PartitionItemRepository_IntegTest {

        @Test
        public void doublePartitionItem() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            PartitionItem partitionItem = budget.getItems().first().getPartitionItems().get(0);

            //when, then
            assertThat(partitionItemRepository
                    .validateNewPartitionItem(
                            budget.getPartitionings().first(),
                            partitionItem.getCharge(),
                            partitionItem.getKeyTable(),
                            partitionItem.getBudgetItem(),
                            null)
            ).isEqualTo("This partition item already exists");

        }

    }

    public static class FindByPartitionItem extends PartitionItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
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
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            BudgetItem budgetItem = budget.getItems().first();
            KeyTable keyTable = keytablesRepository.findByBudget(budget).get(0);
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            Partitioning partitioning = budget.getPartitionings().first();
            // when
            final PartitionItem partitionItem = partitionItemRepository.findUnique(partitioning, charge, budgetItem, keyTable);
            // then
            assertThat(partitionItem.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(partitionItem.getKeyTable()).isEqualTo(keyTable);
        }

    }

    public static class UpdateOrCreate extends PartitionItemRepository_IntegTest {

        PartitionItem partitionItem;
        BigDecimal origPercentage;
        BigDecimal newPercentage;


        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            BudgetItem budgetItem = budget.getItems().first();
            KeyTable keyTable = keytablesRepository.findByBudget(budget).get(0);
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            Partitioning partitioning = budget.getPartitionings().first();

            origPercentage = new BigDecimal("100").setScale(6, BigDecimal.ROUND_HALF_UP);
            newPercentage = new BigDecimal("90").setScale(6, BigDecimal.ROUND_HALF_UP);

            partitionItem = partitionItemRepository.findUnique(partitioning, charge, budgetItem, keyTable);
            assertThat(partitionItem.getPercentage()).isEqualTo(origPercentage);

            // when
            partitionItem = partitionItemRepository.updateOrCreatePartitionItem(budget.getPartitionings().first(), budgetItem, charge, keyTable, newPercentage);

            // then
            assertThat(partitionItem.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(partitionItem.getKeyTable()).isEqualTo(keyTable);
            assertThat(partitionItem.getPercentage()).isEqualTo(newPercentage);
        }

    }

}
