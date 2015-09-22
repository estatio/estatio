/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.integtests.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitMenu;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.budget.KeyTablesForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTableTest extends EstatioIntegrationTest {


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

    @Inject
    KeyTables tables;

    protected KeyTable keyTable =  new KeyTable();

    public static class AllKeyTables extends KeyTableTest {

        @Test
        public void whenExists() throws Exception {
            assertThat(tables.allKeyTables().size()).isGreaterThan(0);
        }
    }

    public static class changeKeyTable extends KeyTableTest {

        @Test
        public void whenSetUp() throws Exception {

            //given
            keyTable = tables.findByName(KeyTablesForOxf.NAME);
            assertThat(keyTable.getName().equals(KeyTablesForOxf.NAME));
            assertThat(keyTable.getStartDate().equals(KeyTablesForOxf.STARTDATE));
            assertThat(keyTable.getEndDate().equals(KeyTablesForOxf.ENDDATE));
            assertThat(keyTable.getFoundationValueType().equals(KeyTablesForOxf.BUDGET_FOUNDATION_VALUE_TYPE));
            assertThat(keyTable.getKeyValueMethod().equals(KeyTablesForOxf.BUDGET_KEY_VALUE_METHOD));
            assertThat(keyTable.isValidForKeyValues() == true);


            //when
            keyTable.changeName("something else");
            keyTable.changeDates(new LocalDate(2015, 07, 01), new LocalDate(2015, 12, 31));
            keyTable.changeFoundationValueType(FoundationValueType.COUNT);
            keyTable.changeKeyValueMethod(KeyValueMethod.DEFAULT);

            //then
            assertThat(keyTable.getName().equals("something else"));
            assertThat(keyTable.getStartDate().equals(new LocalDate(2015, 07, 01)));
            assertThat(keyTable.getEndDate().equals(new LocalDate(2015,12,31)));
            assertThat(keyTable.getFoundationValueType().equals(FoundationValueType.COUNT));
            assertThat(keyTable.getKeyValueMethod().equals(KeyValueMethod.DEFAULT));
            //due to changing KeyValueMethod
            assertThat(keyTable.isValidForKeyValues() == false);
        }


    }

    public static class generateKeyItemsTest extends KeyTableTest {

        @Inject
        KeyItems items;

        @Inject
        UnitMenu unitMenu;
        @Inject
        UnitRepository unitRepository;

        @Test
        public void whenSetUp() throws Exception {

            //given
            keyTable = tables.findByName(KeyTablesForOxf.NAME);

            //when
            keyTable.generateItems(true);

            //then
            assertThat(items.findByKeyTableAndUnit(keyTable, unitRepository.findUnitByReference("OXF-001")).getValue().equals(new BigDecimal(3)));
            assertThat(items.findByKeyTableAndUnit(keyTable, unitRepository.findUnitByReference("OXF-002")).getValue().equals(new BigDecimal(6)));
        }

        Unit unitWithAreaNull;
        @Test
        public void whenSetUpWithNullValues() throws Exception {

            //given
            keyTable = tables.findByName(KeyTablesForOxf.NAME);
            unitWithAreaNull = unitRepository.findUnitByReference("OXF-001");
            unitWithAreaNull.setArea(null);

            //when
            keyTable.generateItems(true);

            //then
            assertThat(items.findByKeyTableAndUnit(keyTable, unitWithAreaNull).getValue().equals(BigDecimal.ZERO));
            assertThat(items.findByKeyTableAndUnit(keyTable, unitRepository.findUnitByReference("OXF-002")).getValue().equals(new BigDecimal(6)));
        }

        Unit unitNotIncluded;
        Unit unitNotIncludedWithEndDateOnly;
        Unit unitNotIncludedWithStartDateOnly;
        Unit unitIncluded;
        Unit unitIncludedWithEndDateOnly;
        Unit unitIncludedWithStartDateOnly;
        Unit unitIncludedWithoutStartAndEndDate;
        @Test
        public void whenSetUpWithUnitsNotInKeyTablePeriod() throws Exception {

            //given
            keyTable = tables.findByName(KeyTablesForOxf.NAME);

            //when
            unitNotIncludedWithEndDateOnly = unitRepository.findUnitByReference("OXF-001");
            unitNotIncludedWithEndDateOnly.setStartDate(null);
            unitNotIncludedWithEndDateOnly.setEndDate(new LocalDate(2015, 12, 30));
            unitNotIncluded = unitRepository.findUnitByReference("OXF-002");
            unitNotIncluded.setStartDate(new LocalDate(2015, 01, 01));
            unitNotIncluded.setEndDate(new LocalDate(2015, 12, 30));
            unitNotIncludedWithStartDateOnly = unitRepository.findUnitByReference("OXF-003");
            unitNotIncludedWithStartDateOnly.setStartDate(new LocalDate(2015, 01, 02));
            unitNotIncludedWithStartDateOnly.setEndDate(null);

            unitIncluded = unitRepository.findUnitByReference("OXF-004");
            unitIncluded.setStartDate(new LocalDate(2015, 01, 01));
            unitIncluded.setEndDate(new LocalDate(2015, 12, 31));
            unitIncludedWithEndDateOnly = unitRepository.findUnitByReference("OXF-005");
            unitIncludedWithEndDateOnly.setStartDate(null);
            unitIncludedWithEndDateOnly.setEndDate(new LocalDate(2015, 12, 31));
            unitIncludedWithStartDateOnly = unitRepository.findUnitByReference("OXF-006");
            unitIncludedWithStartDateOnly.setStartDate(new LocalDate(2015,01,01));
            unitIncludedWithStartDateOnly.setEndDate(null);
            unitIncludedWithoutStartAndEndDate = unitRepository.findUnitByReference("OXF-007");
            unitIncludedWithoutStartAndEndDate.setStartDate(null);
            unitIncludedWithoutStartAndEndDate.setEndDate(null);

            keyTable.generateItems(true);

            //then
            assertThat(keyTable.getStartDate().equals(new LocalDate(2015,01,01)));
            assertThat(keyTable.getStartDate().equals(new LocalDate(2015,12,31)));
            Assert.assertNull(items.findByKeyTableAndUnit(keyTable, unitNotIncludedWithEndDateOnly));
            Assert.assertNull(items.findByKeyTableAndUnit(keyTable, unitNotIncluded));
            Assert.assertNull(items.findByKeyTableAndUnit(keyTable, unitNotIncludedWithStartDateOnly));
            assertThat(items.findByKeyTableAndUnit(keyTable, unitIncluded).getValue().equals(new BigDecimal(6)));
            assertThat(items.findByKeyTableAndUnit(keyTable, unitIncludedWithEndDateOnly).getValue().equals(new BigDecimal(6)));
            assertThat(items.findByKeyTableAndUnit(keyTable, unitIncludedWithStartDateOnly).getValue().equals(new BigDecimal(6)));
            assertThat(items.findByKeyTableAndUnit(keyTable, unitIncludedWithoutStartAndEndDate).getValue().equals(new BigDecimal(6)));

        }



    }


}