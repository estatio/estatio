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

package org.estatio.module.budget.fixtures.keytables.enums;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.EnumWithBuilderScript;
import org.apache.isis.applib.fixturescripts.EnumWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.builders.KeyTableBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum KeyTable_enum implements EnumWithBuilderScript<KeyTable, KeyTableBuilder>, EnumWithFinder<KeyTable> {

    Oxf2015Area(
            Budget_enum.OxfBudget2015, "Service Charges By Area year 2015",
            FoundationValueType.AREA, KeyValueMethod.PROMILLE, 3),
    Oxf2015Count(
            Budget_enum.OxfBudget2015, "Service Charges By Count year 2015",
            FoundationValueType.COUNT, KeyValueMethod.PROMILLE, 3),;

    private final Budget_enum budget_d;
    private final String name;
    private final FoundationValueType foundationValueType;
    private final KeyValueMethod keyValueMethod;
    private final int numberOfDigits;

    public LocalDate getStartDate() {
        return budget_d.getStartDate();
    }

    @Override public KeyTableBuilder toFixtureScript() {
        return new KeyTableBuilder()
                .setPrereq((f,ec) -> f.setBudget(f.objectFor(budget_d, ec)))
                .setName(name)
                .setFoundationValueType(foundationValueType)
                .setKeyValueMethod(keyValueMethod)
                .setNumberOfDigits(numberOfDigits)
                ;
    }

    @Override
    public KeyTable findUsing(final ServiceRegistry2 serviceRegistry) {

        final Budget budget = budget_d.findUsing(serviceRegistry);
        final KeyTableRepository keyTableRepository = serviceRegistry.lookupService(KeyTableRepository.class);

        return keyTableRepository.findByBudgetAndName(budget, name);
    }
}
