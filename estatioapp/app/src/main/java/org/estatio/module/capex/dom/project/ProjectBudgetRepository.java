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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.numerator.dom.Numerator;

@DomainService(repositoryFor = ProjectBudget.class, nature = NatureOfService.DOMAIN)
public class ProjectBudgetRepository extends UdoDomainRepositoryAndFactory<ProjectBudget> {

    public ProjectBudgetRepository() {
        super(ProjectBudgetRepository.class, ProjectBudget.class);
    }

    public List<ProjectBudget> listAll() {
        return allInstances();
    }

    public ProjectBudget findUnique(final Project project, final int version) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        ProjectBudget.class,
                        "findUnique",
                        "project", project,
                        "budgetVersion", version));
    }

    public List<ProjectBudget> findByProject(final Project project) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        ProjectBudget.class,
                        "findByProject",
                        "project", project));
    }

    public ProjectBudget create(
            final Project project,
            final int version) {
        ProjectBudget budget = new ProjectBudget(project, version);
        serviceRegistry2.injectServicesInto(budget);
        repositoryService.persistAndFlush(budget);
        return budget;
    }

    public ProjectBudget findOrCreate(
            final Project project,
            final int version) {
        ProjectBudget budget = findUnique(project, version);
        if(budget == null) {
            budget = create(project, version);
        }
        return budget;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
