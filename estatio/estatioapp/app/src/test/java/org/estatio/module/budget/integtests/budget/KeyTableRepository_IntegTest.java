package org.estatio.module.budget.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTableRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    PartitioningTableRepository keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    final static String TABLE_NAME_1 = "Table 1";
    final static String TABLE_NAME_2 = "Table 2";

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());

            }
        });
    }

    public static class FindByBudgetAndName extends KeyTableRepository_IntegTest {


        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            budget.createKeyTable(TABLE_NAME_1, FoundationValueType.AREA, KeyValueMethod.PROMILLE);

            // when
            final PartitioningTable keyTable = keyTableRepository.findByBudgetAndName(budget, TABLE_NAME_1);
            // then
            assertThat(keyTable.getName()).isEqualTo(TABLE_NAME_1);
            assertThat(keyTable.getBudget().getProperty()).isEqualTo(property);

        }

    }

    public static class FindByBudget extends KeyTableRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            budget.createKeyTable(TABLE_NAME_1, FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            budget.createKeyTable(TABLE_NAME_2, FoundationValueType.AREA, KeyValueMethod.PROMILLE);

            // when
            final List<PartitioningTable> keyTables = keyTableRepository.findByBudget(budget);
            // then
            assertThat(keyTables.size()).isEqualTo(2);
            assertThat(keyTables.get(0).getBudget()).isEqualTo(budget);
        }

    }

}
