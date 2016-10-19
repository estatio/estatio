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

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.keyitem.KeyItem;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, auditing = Auditing.DISABLED)
public class BudgetCalculationViewmodel {

    public BudgetCalculationViewmodel(
            final BudgetItemAllocation itemAllocation,
            final KeyItem keyItem,
            final BigDecimal value,
            final BudgetCalculationType calculationType) {
        this.budgetItemAllocation = itemAllocation;
        this.keyItem = keyItem;
        this.value = value;
        this.calculationType = calculationType;
    }

    @Getter @Setter
    private BigDecimal value;

    @Getter @Setter
    private BudgetItemAllocation budgetItemAllocation;

    @Getter @Setter
    private KeyItem keyItem;
    
    @Getter @Setter
    private BudgetCalculationType calculationType;


}
