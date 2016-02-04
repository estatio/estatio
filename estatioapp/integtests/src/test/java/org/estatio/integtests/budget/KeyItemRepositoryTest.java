package org.estatio.integtests.budget;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItemRepository;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.KeyTablesForOxf;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyItemRepositoryTest extends EstatioIntegrationTest {

    @Inject
    KeyItemRepository keyItemRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    KeyTableRepository keyTableRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new KeyTablesForOxf());
            }
        });
    }

    public static class FindByKeyTableAndUnit extends KeyItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByProperty(property).get(0);
            Unit unit = unitRepository.findByProperty(property).get(0);
            KeyTable keyTable = keyTableRepository.findByBudget(budget).get(0);

            // when
            final KeyItem item = keyItemRepository.findByKeyTableAndUnit(keyTable, unit);

            // then
            assertThat(item.getUnit()).isEqualTo(unit);
            assertThat(item.getKeyTable()).isEqualTo(keyTable);
            assertThat(item.getKeyTable().getBudget()).isEqualTo(budget);
        }

    }

}
