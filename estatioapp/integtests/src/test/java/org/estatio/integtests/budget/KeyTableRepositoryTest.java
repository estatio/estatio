package org.estatio.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
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
public class KeyTableRepositoryTest extends EstatioIntegrationTest {

    @Inject
    KeyTables keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

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

    public static class FindByProperty extends KeyTableRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final List<KeyTable> keyTableList = keyTableRepository.findByProperty(property);
            // then
            assertThat(keyTableList.size()).isEqualTo(2);

        }

    }

    public static class FindByPropertyAndNameAndStartDate extends KeyTableRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final KeyTable keyTable = keyTableRepository.findByPropertyAndNameAndStartDate(property, KeyTablesForOxf.NAME, KeyTablesForOxf.STARTDATE);
            // then
            assertThat(keyTable.getName()).isEqualTo(KeyTablesForOxf.NAME);
            assertThat(keyTable.getProperty()).isEqualTo(property);

        }

    }

}
