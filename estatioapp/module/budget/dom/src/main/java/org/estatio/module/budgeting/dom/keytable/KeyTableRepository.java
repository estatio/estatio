/*
 * Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.module.budgeting.dom.keytable;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budgeting.dom.budget.Budget;

import java.util.List;

@DomainService(repositoryFor = KeyTable.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class KeyTableRepository extends UdoDomainRepositoryAndFactory<KeyTable> {

    public KeyTableRepository() {
        super(KeyTableRepository.class, KeyTable.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public KeyTable newKeyTable(
            final Budget budget,
            final String name,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits) {
        KeyTable keyTable = newTransientInstance();
        keyTable.setBudget(budget);
        keyTable.setName(name);
        keyTable.setFoundationValueType(foundationValueType);
        keyTable.setKeyValueMethod(keyValueMethod);
        keyTable.setPrecision(numberOfDigits);
        persistIfNotAlready(keyTable);

        return keyTable;
    }

    public String validateNewKeyTable(
            final Budget budget,
            final String name,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits) {
        if (findByBudgetAndName(budget, name)!=null) {
            return "There is already a keytable with this name for this budget";
        }

        return null;
    }

    @Programmatic
    public KeyTable findOrCreateBudgetKeyTable(
            final Budget budget,
            final String name,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer precision
    ) {
        final KeyTable keyTable = findByBudgetAndName(budget, name);
        if (keyTable !=null) {
            return keyTable;
        } else {
            return newKeyTable(budget, name, foundationValueType, keyValueMethod, precision);
        }
    }


    @Programmatic
    public List<KeyTable> allKeyTables() {
        return allInstances();
    }


    @Programmatic
    public KeyTable findByBudgetAndName(final Budget budget, final String name) {
        return uniqueMatch("findByBudgetAndName", "budget", budget, "name", name);
    }


    public List<KeyTable> findByBudget(Budget budget) {
        return allMatches("findByBudget", "budget", budget);
    }


    @ActionLayout(hidden = Where.EVERYWHERE)
    public List<KeyTable> autoComplete(final String search) {
        return allMatches("findKeyTableByNameMatches", "name", search.toLowerCase());
    }

}
