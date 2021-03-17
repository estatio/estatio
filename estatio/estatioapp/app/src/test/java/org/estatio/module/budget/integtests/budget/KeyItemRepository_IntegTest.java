package org.estatio.module.budget.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.KeyItemRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyItemRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    KeyItemRepository keyItemRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    BudgetRepository budgetRepository;

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

    public static class FindByKeyTableAndUnit extends KeyItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByProperty(property).get(0);
            KeyTable keyTable = budget.createKeyTable("table", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            keyTable.generateItems();
            Unit unit = unitRepository.findByProperty(property).get(0);

            // when
            final KeyItem item = keyItemRepository.findByKeyTableAndUnit(keyTable, unit);

            // then
            assertThat(item.getUnit()).isEqualTo(unit);
            assertThat(item.getPartitioningTable()).isEqualTo(keyTable);
            assertThat(item.getPartitioningTable().getBudget()).isEqualTo(budget);
        }

    }

}
