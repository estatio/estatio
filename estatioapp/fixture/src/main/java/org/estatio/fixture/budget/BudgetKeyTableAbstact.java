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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budget.BudgetFoundationValueType;
import org.estatio.dom.budget.BudgetKeyItems;
import org.estatio.dom.budget.BudgetKeyTable;
import org.estatio.dom.budget.BudgetKeyTables;
import org.estatio.dom.budget.BudgetKeyValueMethod;
import org.estatio.fixture.EstatioFixtureScript;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class BudgetKeyTableAbstact extends EstatioFixtureScript {

    protected BudgetKeyTable createBudgetKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final BudgetFoundationValueType budgetFoundationValueType,
            final BudgetKeyValueMethod budgetKeyValueMethod,
            final ExecutionContext fixtureResults){
        BudgetKeyTable budgetKeyTable = budgetKeyTables.newBudgetKeyTable(property, name ,startDate, endDate, budgetFoundationValueType, budgetKeyValueMethod);
        budgetKeyTable.generateBudgetKeyItems(true);
        return fixtureResults.addResult(this, budgetKeyTable);
    }

    @Inject
    protected BudgetKeyTables budgetKeyTables;

    @Inject
    protected BudgetKeyItems budgetKeyItems;

    @Inject
    protected Properties properties;
}
