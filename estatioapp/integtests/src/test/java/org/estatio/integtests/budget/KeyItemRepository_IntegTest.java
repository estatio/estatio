package org.estatio.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budget.BudgetRepository;
import org.estatio.module.budgeting.dom.keyitem.KeyItem;
import org.estatio.module.budgeting.dom.keyitem.KeyItemRepository;
import org.estatio.module.budgeting.dom.keytable.FoundationValueType;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
import org.estatio.module.budgeting.dom.keytable.KeyTableRepository;
import org.estatio.module.budgeting.dom.keytable.KeyValueMethod;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyItemRepository_IntegTest extends EstatioIntegrationTest {

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
                executionContext.executeChild(this, new BudgetsForOxf());
            }
        });
    }

    public static class FindByKeyTableAndUnit extends KeyItemRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByProperty(property).get(0);
            KeyTable keyTable = budget.createKeyTable("table", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            keyTable.generateItems();
            Unit unit = unitRepository.findByProperty(property).get(0);

            // when
            final KeyItem item = keyItemRepository.findByKeyTableAndUnit(keyTable, unit);

            // then
            assertThat(item.getUnit()).isEqualTo(unit);
            assertThat(item.getKeyTable()).isEqualTo(keyTable);
            assertThat(item.getKeyTable().getBudget()).isEqualTo(budget);
        }

    }

}
