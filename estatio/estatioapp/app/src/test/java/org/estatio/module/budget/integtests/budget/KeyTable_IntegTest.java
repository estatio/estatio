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
package org.estatio.module.budget.integtests.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.keyitem.KeyItemRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTable_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    KeyTableRepository keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    KeyItemRepository keyItemRepository;

    @Inject
    UnitRepository unitRepository;

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

    public static class ChangeKeytableTest extends KeyTable_IntegTest {

        KeyTable keyTable;

        @Test
        public void changeKeyTableTest() throws Exception {

            //given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            keyTable = budget.createKeyTable("Some name", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            wrap(keyTable.generateItems());
            transactionService.nextTransaction();
            assertThat(keyTable.isValidForKeyValues()).isEqualTo(true);

            //when
            keyTable.changeName("something else");

            //then
            assertThat(keyTable.getName()).isEqualTo("something else");

        }

    }

    public static class GenerateKeyItemsTest extends KeyTable_IntegTest {

        KeyTable keyTableByArea;

        @Test
        public void whenSetUp() throws Exception {

            //given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            keyTableByArea = budget.createKeyTable("Some name", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            keyTableByArea.setPrecision(3);

            //when
            wrap(keyTableByArea).generateItems();

            //then
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitRepository.findUnitByReference("OXF-001")).getValue()).isEqualTo(new BigDecimal("3.077"));
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitRepository.findUnitByReference("OXF-002")).getValue()).isEqualTo(new BigDecimal("6.154"));
        }

        Unit unitWithAreaNull;

        @Test
        public void whenSetUpWithNullValues() throws Exception {

            //given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            keyTableByArea = budget.createKeyTable("Some name", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            keyTableByArea.setPrecision(3);
            unitWithAreaNull = unitRepository.findUnitByReference("OXF-001");
            unitWithAreaNull.setArea(null);

            //when
            wrap(keyTableByArea).generateItems();

            //then
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitWithAreaNull).getValue()).isEqualTo(new BigDecimal("0.000"));
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitRepository.findUnitByReference("OXF-002")).getValue()).isEqualTo(new BigDecimal("6.173"));
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
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            keyTableByArea = budget.createKeyTable("Some name", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
            keyTableByArea.setPrecision(3);

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
            unitIncludedWithStartDateOnly.setStartDate(new LocalDate(2015, 01, 01));
            unitIncludedWithStartDateOnly.setEndDate(null);
            unitIncludedWithoutStartAndEndDate = unitRepository.findUnitByReference("OXF-007");
            unitIncludedWithoutStartAndEndDate.setStartDate(null);
            unitIncludedWithoutStartAndEndDate.setEndDate(null);

            wrap(keyTableByArea).generateItems();
            transactionService.nextTransaction();

            //then
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitNotIncludedWithEndDateOnly)).isEqualTo(null);
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitNotIncluded)).isEqualTo(null);
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitNotIncludedWithStartDateOnly)).isEqualTo(null);
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitIncluded).getValue()).isEqualTo(new BigDecimal("12.539000"));
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitIncludedWithEndDateOnly).getValue()).isEqualTo(new BigDecimal("15.674000"));
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitIncludedWithStartDateOnly).getValue()).isEqualTo(new BigDecimal("18.808000"));
            assertThat(keyItemRepository.findByKeyTableAndUnit(keyTableByArea, unitIncludedWithoutStartAndEndDate).getValue()).isEqualTo(new BigDecimal("21.944000"));

        }

    }

}