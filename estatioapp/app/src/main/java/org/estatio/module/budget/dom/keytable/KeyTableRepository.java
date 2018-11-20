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
package org.estatio.module.budget.dom.keytable;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budget.Budget;

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
        if (partitioningTableRepository.findByBudgetAndName(budget, name)!=null) {
            return "There is already a table with this name for this budget";
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
        final PartitioningTable keyTableIfAny = partitioningTableRepository.findByBudgetAndName(budget, name);
        if (keyTableIfAny !=null && keyTableIfAny.getClass().isAssignableFrom(KeyTable.class)) {
            return (KeyTable) keyTableIfAny;
        } else {
            return newKeyTable(budget, name, foundationValueType, keyValueMethod, precision);
        }
    }


    public List<KeyTable> autoComplete(final String search) {
        return partitioningTableRepository.autoComplete(search)
                .stream()
                .filter(KeyTable.class::isInstance)
                .map(KeyTable.class::cast)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<KeyTable> allKeyTables() {
        return allInstances();
    }

    @Inject PartitioningTableRepository partitioningTableRepository;

}
