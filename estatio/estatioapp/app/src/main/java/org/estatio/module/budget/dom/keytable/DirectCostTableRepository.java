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

@DomainService(repositoryFor = DirectCostTable.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class DirectCostTableRepository extends UdoDomainRepositoryAndFactory<DirectCostTable> {

    public DirectCostTableRepository() {
        super(DirectCostTableRepository.class, DirectCostTable.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public DirectCostTable newDirectCostTable(
            final Budget budget,
            final String name) {
        DirectCostTable directCostTable = newTransientInstance();
        directCostTable.setBudget(budget);
        directCostTable.setName(name);
        persistIfNotAlready(directCostTable);

        return directCostTable;
    }

    public String validateNewKeyTable(
            final Budget budget,
            final String name) {
        if (partitioningTableRepository.findByBudgetAndName(budget, name)!=null) {
            return "There is already a table with this name for this budget";
        }

        return null;
    }

    @Programmatic
    public DirectCostTable findOrCreateDirectCostTable(
            final Budget budget,
            final String name
    ) {
        final PartitioningTable tableIfAny = partitioningTableRepository.findByBudgetAndName(budget, name);
        if (tableIfAny !=null && tableIfAny.getClass().isAssignableFrom(DirectCostTable.class)) {
            return (DirectCostTable) tableIfAny;
        } else {
            return newDirectCostTable(budget, name);
        }
    }


    public List<DirectCostTable> autoComplete(final String search) {
        return partitioningTableRepository.autoComplete(search)
                .stream()
                .filter(DirectCostTable.class::isInstance)
                .map(DirectCostTable.class::cast)
                .collect(Collectors.toList());
    }

    @Inject
    PartitioningTableRepository partitioningTableRepository;

}
