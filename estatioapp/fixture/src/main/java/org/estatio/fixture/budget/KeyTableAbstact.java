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

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.asset.dom.PropertyRepository;
import org.estatio.budget.dom.budget.Budget;
import org.estatio.budget.dom.budget.BudgetRepository;
import org.estatio.budget.dom.keytable.FoundationValueType;
import org.estatio.budget.dom.keytable.KeyTable;
import org.estatio.budget.dom.keytable.KeyTableRepository;
import org.estatio.budget.dom.keytable.KeyValueMethod;

public abstract class KeyTableAbstact extends FixtureScript {

    protected KeyTable createKeyTable(
            final Budget budget,
            final String name,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits,
            final ExecutionContext fixtureResults){
        KeyTable keyTable = keyTableRepository.newKeyTable(budget, name, foundationValueType, keyValueMethod, numberOfDigits);
        keyTable.generateItems();
        return fixtureResults.addResult(this, keyTable);
    }

    @Inject
    protected KeyTableRepository keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

}
