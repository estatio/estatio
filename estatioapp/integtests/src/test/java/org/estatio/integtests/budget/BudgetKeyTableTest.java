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
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.budget.BudgetFoundationValueType;
import org.estatio.dom.budget.BudgetKeyItemContributions;
import org.estatio.dom.budget.BudgetKeyItems;
import org.estatio.dom.budget.BudgetKeyTable;
import org.estatio.dom.budget.BudgetKeyTables;
import org.estatio.dom.budget.BudgetKeyValueMethod;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.budget.BudgetKeyTablesForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetKeyTableTest extends EstatioIntegrationTest {


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetKeyTablesForOxf());

            }
        });
    }

    @Inject
    BudgetKeyTables tables;

    protected BudgetKeyTable budgetKeyTable =  new BudgetKeyTable();

    public static class AllBudgetKeyTables extends BudgetKeyTableTest {

        @Test
        public void whenExists() throws Exception {
            assertThat(tables.allBudgetKeyTables().size()).isGreaterThan(0);
        }
    }

    public static class changeBudgetKeyTable extends BudgetKeyTableTest {

        @Test
        public void whenSetUp() throws Exception {

            //given
            budgetKeyTable = tables.findBudgetKeyTableByName(BudgetKeyTablesForOxf.NAME);
            assertThat(budgetKeyTable.getName().equals(BudgetKeyTablesForOxf.NAME));
            assertThat(budgetKeyTable.getStartDate().equals(BudgetKeyTablesForOxf.STARTDATE));
            assertThat(budgetKeyTable.getEndDate().equals(BudgetKeyTablesForOxf.ENDDATE));
            assertThat(budgetKeyTable.getFoundationValueType().equals(BudgetKeyTablesForOxf.BUDGET_FOUNDATION_VALUE_TYPE));
            assertThat(budgetKeyTable.getKeyValueMethod().equals(BudgetKeyTablesForOxf.BUDGET_KEY_VALUE_METHOD));
            assertThat(budgetKeyTable.isValidForKeyValues() == true);


            //when
            budgetKeyTable.changeName("something else");
            budgetKeyTable.changeDates(new LocalDate(2015, 07, 01), new LocalDate(2015, 12, 31));
            budgetKeyTable.changeFoundationValueType(BudgetFoundationValueType.COUNT);
            budgetKeyTable.changeKeyValueMethod(BudgetKeyValueMethod.DEFAULT);

            //then
            assertThat(budgetKeyTable.getName().equals("something else"));
            assertThat(budgetKeyTable.getStartDate().equals(new LocalDate(2015, 07, 01)));
            assertThat(budgetKeyTable.getEndDate().equals(new LocalDate(2015,12,31)));
            assertThat(budgetKeyTable.getFoundationValueType().equals(BudgetFoundationValueType.COUNT));
            assertThat(budgetKeyTable.getKeyValueMethod().equals(BudgetKeyValueMethod.DEFAULT));
            //due to changing BudgetKeyValueMethod
            assertThat(budgetKeyTable.isValidForKeyValues() == false);
        }


    }

    public static class generateBudgetKeyItemsTest extends BudgetKeyTableTest {

        @Inject
        BudgetKeyItems items;

        @Inject
        Units units;

        @Test
        public void whenSetUp() throws Exception {

            //given
            budgetKeyTable = tables.findBudgetKeyTableByName(BudgetKeyTablesForOxf.NAME);

            //when
            budgetKeyTable.generateBudgetKeyItems(true);

            //then
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, units.findUnitByReference("OXF-001")).getValue().equals(new BigDecimal(3)));
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, units.findUnitByReference("OXF-002")).getValue().equals(new BigDecimal(6)));
        }

        Unit unitWithAreaNull;
        @Test
        public void whenSetUpWithNullValues() throws Exception {

            //given
            budgetKeyTable = tables.findBudgetKeyTableByName(BudgetKeyTablesForOxf.NAME);
            unitWithAreaNull = units.findUnitByReference("OXF-001");
            unitWithAreaNull.setArea(null);

            //when
            budgetKeyTable.generateBudgetKeyItems(true);

            //then
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitWithAreaNull).getValue().equals(BigDecimal.ZERO));
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, units.findUnitByReference("OXF-002")).getValue().equals(new BigDecimal(6)));
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
            budgetKeyTable = tables.findBudgetKeyTableByName(BudgetKeyTablesForOxf.NAME);

            //when
            unitNotIncludedWithEndDateOnly = units.findUnitByReference("OXF-001");
            unitNotIncludedWithEndDateOnly.setStartDate(null);
            unitNotIncludedWithEndDateOnly.setEndDate(new LocalDate(2015, 12, 30));
            unitNotIncluded = units.findUnitByReference("OXF-002");
            unitNotIncluded.setStartDate(new LocalDate(2015, 01, 01));
            unitNotIncluded.setEndDate(new LocalDate(2015, 12, 30));
            unitNotIncludedWithStartDateOnly = units.findUnitByReference("OXF-003");
            unitNotIncludedWithStartDateOnly.setStartDate(new LocalDate(2015, 01, 02));
            unitNotIncludedWithStartDateOnly.setEndDate(null);

            unitIncluded = units.findUnitByReference("OXF-004");
            unitIncluded.setStartDate(new LocalDate(2015, 01, 01));
            unitIncluded.setEndDate(new LocalDate(2015, 12, 31));
            unitIncludedWithEndDateOnly = units.findUnitByReference("OXF-005");
            unitIncludedWithEndDateOnly.setStartDate(null);
            unitIncludedWithEndDateOnly.setEndDate(new LocalDate(2015, 12, 31));
            unitIncludedWithStartDateOnly = units.findUnitByReference("OXF-006");
            unitIncludedWithStartDateOnly.setStartDate(new LocalDate(2015,01,01));
            unitIncludedWithStartDateOnly.setEndDate(null);
            unitIncludedWithoutStartAndEndDate = units.findUnitByReference("OXF-007");
            unitIncludedWithoutStartAndEndDate.setStartDate(null);
            unitIncludedWithoutStartAndEndDate.setEndDate(null);

            budgetKeyTable.generateBudgetKeyItems(true);

            //then
            assertThat(budgetKeyTable.getStartDate().equals(new LocalDate(2015,01,01)));
            assertThat(budgetKeyTable.getStartDate().equals(new LocalDate(2015,12,31)));
            Assert.assertNull(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitNotIncludedWithEndDateOnly));
            Assert.assertNull(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitNotIncluded));
            Assert.assertNull(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitNotIncludedWithStartDateOnly));
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitIncluded).getValue().equals(new BigDecimal(6)));
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitIncludedWithEndDateOnly).getValue().equals(new BigDecimal(6)));
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitIncludedWithStartDateOnly).getValue().equals(new BigDecimal(6)));
            assertThat(items.findByBudgetKeyTableAndUnit(budgetKeyTable, unitIncludedWithoutStartAndEndDate).getValue().equals(new BigDecimal(6)));

        }



    }

    public static class validateNewBudgetKeyItemTest extends BudgetKeyTableTest {

        @Inject
        BudgetKeyItemContributions items;

        @Inject
        Units units;

        BigDecimal newKeyValue;
        BigDecimal newPositiveKeyValue;
        Unit unit;

        @Test
        public void whenSetup() throws Exception {

            //given
            budgetKeyTable = tables.findBudgetKeyTableByName(BudgetKeyTablesForOxf.NAME);
            unit = units.findUnitByReference("OXF-001");

            //when
            newKeyValue = new BigDecimal(-0.001);

            // then
            assertThat(items.validateNewBudgetKeyItem(budgetKeyTable, unit, newKeyValue).equals("keyValue cannot be less than zero"));

            //when
            newPositiveKeyValue = new BigDecimal(0.001);

            // then
            assertThat(items.validateNewBudgetKeyItem(budgetKeyTable, unit, newKeyValue).equals("Augmented keyValue cannot be less than zero"));

        }
    }


}