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

package org.estatio.fixture.budget;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.asset.dom.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.dom.charge.Charge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForBudNl;
import org.estatio.fixture.charge.ChargeRefData;

public class PartitionItemsForBud extends PartitionItemAbstact {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new KeyTablesForBud());
        executionContext.executeChild(this, new PartitioningForBud());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForBudNl.REF);
        LocalDate startDate = new LocalDate(2015, 01, 01);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);
        Charge incomingCharge1 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_1);
        Charge incomingCharge2 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_2);
        Charge incomingCharge3 = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_3);
        BudgetItem budgetItem1 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge1);
        BudgetItem budgetItem2 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge2);
        BudgetItem budgetItem3 = budgetItemRepository.findByBudgetAndCharge(budget, incomingCharge3);
        Charge invoiceCharge1 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE);
        Charge invoiceCharge2 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2);
        KeyTable keyTable1 = keyTableRepository.findByBudgetAndName(budget, KeyTablesForBud.NAME_BY_AREA);
        KeyTable keyTable2 = keyTableRepository.findByBudgetAndName(budget, KeyTablesForBud.NAME_BY_COUNT);
        Partitioning partitioning = budget.getPartitionings().first();

        createPartitioningAndItem(partitioning, invoiceCharge1, keyTable1, budgetItem1, new BigDecimal(100), executionContext);
        createPartitioningAndItem(partitioning, invoiceCharge1, keyTable1, budgetItem2, new BigDecimal(80), executionContext);
        createPartitioningAndItem(partitioning, invoiceCharge1, keyTable2, budgetItem2, new BigDecimal(20), executionContext);
        createPartitioningAndItem(partitioning, invoiceCharge2, keyTable1, budgetItem3, new BigDecimal(90), executionContext);
        createPartitioningAndItem(partitioning, invoiceCharge1, keyTable2, budgetItem3, new BigDecimal(10), executionContext);
    }

}
