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

import org.joda.time.LocalDate;

import org.estatio.asset.dom.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForBudNl;

public class PartitioningForBud extends PartitionItemAbstact {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new BudgetForBud());

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForBudNl.REF);
        LocalDate startDate = new LocalDate(2015, 01, 01);
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, startDate);

        createPartitioning(budget, executionContext);

    }

}
