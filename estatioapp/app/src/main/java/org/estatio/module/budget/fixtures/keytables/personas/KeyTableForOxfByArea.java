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

package org.estatio.module.budget.fixtures.keytables.personas;

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.fixtures.budgets.personas.BudgetsForOxf;
import org.estatio.module.budget.fixtures.keytables.KeyTableAbstract;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;

public class KeyTableForOxfByArea extends KeyTableAbstract {

    public static final KeyTable_enum data = KeyTable_enum.Oxf2015Area;

    public static final String NAME = data.getName();
    public static final FoundationValueType FOUNDATION_VALUE_TYPE = data.getFoundationValueType();
    public static final KeyValueMethod KEY_VALUE_METHOD = data.getKeyValueMethod();

    public static final LocalDate START_DATE = data.getStartDate();
    public static final int NUMBER_OF_DIGITS = data.getNumberOfDigits();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new BudgetsForOxf());

        // exec
        Property property = propertyRepository.findPropertyByReference(
                Property_enum.OxfGb.getRef());
        Budget budget = budgetRepository.findByPropertyAndStartDate(property, START_DATE);

        createKeyTable(budget, data.getName(), data.getFoundationValueType(), data.getKeyValueMethod(), data.getNumberOfDigits(), executionContext);
    }
}
