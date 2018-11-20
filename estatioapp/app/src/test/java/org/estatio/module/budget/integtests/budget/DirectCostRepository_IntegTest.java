package org.estatio.module.budget.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.DirectCostRepository;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectCostRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    DirectCostRepository directCostRepository;

    @Inject
    DirectCostTableRepository directCostTableRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
            }
        });
    }

    @Test
    public void find_by_direct_cost_table_and_unit_works() throws Exception {

        // given
        final Budget budget = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        final String directTableName = "direct";
        DirectCostTable table = directCostTableRepository.findOrCreateDirectCostTable(budget, directTableName);

        // when
        wrap(table).generateItems();
        final DirectCost firstItem = table.getItems().first();
        Unit unitOnFirstItem = firstItem.getUnit();

        // then
        assertThat(directCostRepository.findByDirectCostTableAndUnit(table, unitOnFirstItem)).isSameAs(firstItem);

    }

}
