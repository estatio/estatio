/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.isisaddons.module.excel.dom.PivotColumn;
import org.isisaddons.module.excel.dom.PivotDecoration;
import org.isisaddons.module.excel.dom.PivotRow;
import org.isisaddons.module.excel.dom.PivotValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "budgetcalculation.CalculationVMForUnit")
@AllArgsConstructor
@Getter @Setter
public class CalculationVMForUnit {

    @PivotRow
    private String budgetItemChargeReferenceAndPartitioning;

    @PivotDecoration(order = 1)
    @Digits(integer=13, fraction = 2)
    private BigDecimal budgetItemAmount;

    @PivotDecoration(order = 2)
    private String calculationDescription;

    @PivotColumn(order = 1)
    private String tableNameAndSourceValue;

    @Digits(integer=13, fraction = 2)
    @PivotValue(order = 1)
    private BigDecimal value;

}
