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
package org.estatio.dom.budgeting.budgetcalculation;

import lombok.Getter;
import lombok.Setter;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.keyitem.KeyItem;

import java.math.BigDecimal;

public class BudgetCalculationResult implements Distributable {

    public BudgetCalculationResult(
            final BudgetItemAllocation itemAllocation,
            final KeyItem keyItem,
            final BigDecimal value,
            final BigDecimal sourceValue,
            final CalculationType calculationType) {
        this.budgetItemAllocation = itemAllocation;
        this.keyItem = keyItem;
        this.value = value;
        this.sourceValue = sourceValue;
        this.calculationType = calculationType;
    }

    @Getter @Setter
    private BigDecimal value;

    @Getter @Setter
    private BudgetItemAllocation budgetItemAllocation;

    @Getter @Setter
    private KeyItem keyItem;

    @Getter @Setter
    private BigDecimal sourceValue;

    @Getter @Setter
    private CalculationType calculationType;


}
