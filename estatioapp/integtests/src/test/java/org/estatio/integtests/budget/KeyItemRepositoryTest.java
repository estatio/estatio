package org.estatio.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.KeyTablesForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class KeyItemRepositoryTest extends EstatioIntegrationTest {

    @Inject
    KeyItems keyItemRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    KeyTables keyTableRepository;

    @Inject

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
            Unit unit = unitRepository.findByProperty(property).get(0);
            KeyTable keyTable = keyTableRepository.findByProperty(property).get(0);
            // when
            final KeyItem item = keyItemRepository.findByKeyTableAndUnit(keyTable, unit);
            // then
            assertThat(item.getUnit()).isEqualTo(unit);
            assertThat(item.getKeyTable()).isEqualTo(keyTable);
            assertThat(item.getKeyTable().getProperty()).isEqualTo(property);
        }

    }

}
