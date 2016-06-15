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

import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.fixture.EstatioFixtureScript;

import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class BudgetItemAllocationAbstact extends EstatioFixtureScript {

    protected BudgetItemAllocation createBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage,
            final ExecutionContext fixtureResults
    ){
        BudgetItemAllocation budgetItemAllocation = budgetItemAllocationRepository.newBudgetItemAllocation(charge,keyTable,budgetItem, percentage);

        return fixtureResults.addResult(this, budgetItemAllocation);
    }

    @Inject
    protected BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected BudgetRepository budgetRepository;

    @Inject
    protected ChargeRepository chargeRepository;

    @Inject
    protected KeyTableRepository keyTableRepository;

}
