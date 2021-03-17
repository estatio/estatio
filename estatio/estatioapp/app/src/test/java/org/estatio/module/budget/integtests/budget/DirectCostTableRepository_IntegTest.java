package org.estatio.module.budget.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectCostTableRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    DirectCostTableRepository directCostTableRepository;

    @Inject
    PartitioningTableRepository partitioningTableRepository;

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
    public void find_or_create_works() throws Exception {

        // given
        final Budget budget = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        assertThat(budget.getDirectCostTables()).hasSize(0);

        // when
        final String directTableName = "direct";
        directCostTableRepository.findOrCreateDirectCostTable(budget, directTableName);
        transactionService.nextTransaction();

        // then
        final PartitioningTable directCostTable = partitioningTableRepository.findByBudgetAndName(budget, directTableName);
        assertThat(directCostTable).isNotNull();
        assertThat(budget.getDirectCostTables()).hasSize(1);

        // and when again
        directCostTableRepository.findOrCreateDirectCostTable(budget, directTableName);
        transactionService.nextTransaction();

        // then still
        assertThat(budget.getDirectCostTables()).hasSize(1);
        assertThat(budget.getDirectCostTables().first()).isSameAs(directCostTable);

    }

    @Test
    public void auto_complete_works() throws Exception {

        // given
        final Budget budget = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        final String table1 = "direct 1";
        directCostTableRepository.findOrCreateDirectCostTable(budget, table1);
        final String table2 = "direct 2";
        directCostTableRepository.findOrCreateDirectCostTable(budget, table2);
        transactionService.nextTransaction();
        assertThat(budget.getDirectCostTables()).hasSize(2);

        // when, then
        assertThat(directCostTableRepository.autoComplete("dir")).hasSize(2);
        assertThat(directCostTableRepository.autoComplete("1")).hasSize(1);
        assertThat(directCostTableRepository.autoComplete("direct1")).isEmpty();

    }



}
