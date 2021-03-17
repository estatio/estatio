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

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.budget.Budget;

@DomainService(repositoryFor = PartitioningTable.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class PartitioningTableRepository extends UdoDomainRepositoryAndFactory<PartitioningTable> {

    public PartitioningTableRepository() {
        super(PartitioningTableRepository.class, PartitioningTable.class);
    }

    @Programmatic
    public PartitioningTable findByBudgetAndName(final Budget budget, final String name) {
        return uniqueMatch("findByBudgetAndName", "budget", budget, "name", name);
    }


    public List<PartitioningTable> findByBudget(Budget budget) {
        return allMatches("findByBudget", "budget", budget);
    }


    @ActionLayout(hidden = Where.EVERYWHERE)
    public List<PartitioningTable> autoComplete(final String search) {
        return allMatches("findKeyTableByNameMatches", "name", search.toLowerCase());
    }

}
