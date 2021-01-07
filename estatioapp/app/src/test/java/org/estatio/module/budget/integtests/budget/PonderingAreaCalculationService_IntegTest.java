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

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.ponderingareacalculation.PonderingAreaCalculationService;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class PonderingAreaCalculationService_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    UnitRepository unitRepository;

    @Inject
    PonderingAreaCalculationService ponderingAreaCalculationService;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.RonItaBudget2016UnitAreaDivided.builder());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());
            }
        });
    }


    public static class CalculateTotalPonderingAreaForUnitIfPossibleTest extends PonderingAreaCalculationService_IntegTest {

        @Test
        public void whenAreaIsDivided() throws Exception {

            //given
            Unit unit1 = unitRepository.findUnitByReference("RON-001");
            Unit unit2 = unitRepository.findUnitByReference("RON-002");
            Unit unit29 = unitRepository.findUnitByReference("RON-029");
            Unit unit30 = unitRepository.findUnitByReference("RON-030");

            // when
            BigDecimal result1 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit1);
            BigDecimal result2 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit2);
            BigDecimal result29 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit29);
            BigDecimal result30 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit30);

            //then
            assertThat(result1).isEqualTo(new BigDecimal("70.0000"));
            assertThat(result2).isEqualTo(new BigDecimal("140.0000"));
            assertThat(result29).isEqualTo(new BigDecimal("1590.0000"));
            assertThat(result30).isEqualTo(new BigDecimal("1640.0000"));
        }

        @Test
        public void whenAreaIsNotDivided() throws Exception {

            //given
            Unit unit1 = unitRepository.findUnitByReference("OXF-001");
            Unit unit2 = unitRepository.findUnitByReference("OXF-002");
            Unit unit24 = unitRepository.findUnitByReference("OXF-024");
            Unit unit25 = unitRepository.findUnitByReference("OXF-025");

            // when
            BigDecimal result1 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit1);
            BigDecimal result2 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit2);
            BigDecimal result24 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit24);
            BigDecimal result25 = ponderingAreaCalculationService.calculateTotalPonderingAreaForUnitIfPossible(unit25);

            //then
            assertThat(result1).isEqualTo(unit1.getArea());
            assertThat(result2).isEqualTo(unit2.getArea());
            assertThat(result24).isEqualTo(unit24.getArea());
            assertThat(result25).isEqualTo(unit25.getArea());
        }

    }

}