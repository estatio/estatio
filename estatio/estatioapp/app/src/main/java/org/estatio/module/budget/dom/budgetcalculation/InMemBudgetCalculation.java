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

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keyitem.PartitioningTableItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class InMemBudgetCalculation {

    private BigDecimal value;

    private LocalDate calculationStartDate;

    private LocalDate calculationEndDate;

    private PartitionItem partitionItem;

    private PartitioningTableItem tableItem;

    private BudgetCalculationType calculationType;

    private Budget budget;

    private Unit unit;

    private Charge invoiceCharge;

    private Charge incomingCharge;

    private BigDecimal auditedCostForBudgetPeriod;

}
