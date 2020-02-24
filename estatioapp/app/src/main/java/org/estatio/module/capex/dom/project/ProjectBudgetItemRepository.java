/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = ProjectBudgetItem.class, nature = NatureOfService.DOMAIN)
public class ProjectBudgetItemRepository extends UdoDomainRepositoryAndFactory<ProjectBudgetItem> {

    public ProjectBudgetItemRepository() {
        super(ProjectBudgetItemRepository.class, ProjectBudgetItem.class);
    }

    public List<ProjectBudgetItem> listAll() {
        return allInstances();
    }

    public ProjectBudgetItem findUnique(final ProjectBudget budget, final ProjectItem projectItem) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        ProjectBudgetItem.class,
                        "findUnique",
                        "projectBudget", budget,
                        "projectItem", projectItem));
    }

    public ProjectBudgetItem create(
            final ProjectBudget budget,
            final ProjectItem projectItem,
            @Nullable
            final BigDecimal amount) {
        ProjectBudgetItem budgetItem = new ProjectBudgetItem(budget, projectItem, amount);
        serviceRegistry2.injectServicesInto(budgetItem);
        repositoryService.persistAndFlush(budgetItem);
        return budgetItem;
    }
    
    public ProjectBudgetItem findOrCreate(
            final ProjectBudget budget,
            final ProjectItem projectItem,
            @Nullable
            final BigDecimal amount) {
        ProjectBudgetItem budgetItem = findUnique(budget, projectItem);
        if(budgetItem == null) {
            budgetItem = create(budget, projectItem, amount);
        }
        return budgetItem;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
