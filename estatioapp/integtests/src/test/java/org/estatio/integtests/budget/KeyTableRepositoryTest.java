package org.estatio.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.budget.KeyTablesForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTableRepositoryTest extends EstatioIntegrationTest {

    @Inject
    KeyTableRepository keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
                executionContext.executeChild(this, new KeyTablesForOxf());
            }
        });
    }


    public static class FindByBudgetAndName extends KeyTableRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);

            // when
            final KeyTable keyTable = keyTableRepository.findByBudgetAndName(budget, KeyTablesForOxf.NAME_BY_AREA);
            // then
            assertThat(keyTable.getName()).isEqualTo(KeyTablesForOxf.NAME_BY_AREA);
            assertThat(keyTable.getBudget().getProperty()).isEqualTo(property);

        }

    }

    public static class FindByBudget extends KeyTableRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);

            // when
            final List<KeyTable> keyTables = keyTableRepository.findByBudget(budget);
            // then
            assertThat(keyTables.size()).isEqualTo(2);
            assertThat(keyTables.get(0).getBudget()).isEqualTo(budget);
        }

    }

    public static class AllKeyTables extends KeyTableIntegrationTest {

        @Test
        public void allKeytablesTest() throws Exception {
            assertThat(keyTableRepository.allKeyTables().size()).isEqualTo(2);
        }
    }

    @Inject
    BudgetRepository budgetRepository;

}
