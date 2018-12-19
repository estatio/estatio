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
package org.estatio.module.budget.dom.keyitem;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.keytable.DirectCostTable;

@DomainService(repositoryFor = DirectCost.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class DirectCostRepository extends UdoDomainRepositoryAndFactory<DirectCost> {

    public DirectCostRepository() {
        super(DirectCostRepository.class, DirectCost.class);
    }

    public DirectCost newDirectCost(
            final DirectCostTable directCostTable,
            final Unit unit,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue) {
        DirectCost directCost = new DirectCost(
                directCostTable,
                unit,
                budgetedValue,
                auditedValue
        );
        serviceRegistry2.injectServicesInto(directCost);
        repositoryService.persistAndFlush(directCost);

        return directCost;
    }

    public String validateDirectCost(
            final DirectCostTable directCostTable,
            final Unit unit,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue) {

        if (budgetedValue.compareTo(BigDecimal.ZERO) <= 0) {
            return "budgeted value cannot be zero or less than zero";
        }

        if (auditedValue.compareTo(BigDecimal.ZERO) < 0) {
            return "audited value cannot be less than zero";
        }

        if (findByDirectCostTableAndUnit(directCostTable, unit)!=null) {
            return "there is already a direct cost for this unit";
        }

        return null;
    }

    @Programmatic
    public DirectCost findByDirectCostTableAndUnit(DirectCostTable directCostTable, Unit unit){
        final PartitioningTableItem item = partitioningTableItemRepository.findByPartitioningTableAndUnit(directCostTable, unit);
        return item!=null && item.getClass().isAssignableFrom(DirectCost.class) ? (DirectCost) item : null;
    }

    @Inject
    PartitioningTableItemRepository partitioningTableItemRepository;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject RepositoryService repositoryService;

}
