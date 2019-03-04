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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = ProjectItemTerm.class, nature = NatureOfService.DOMAIN)
public class ProjectItemTermRepository extends UdoDomainRepositoryAndFactory<ProjectItemTerm> {

    public ProjectItemTermRepository() {
        super(ProjectItemTermRepository.class, ProjectItemTerm.class);
    }

    @Programmatic
    public List<ProjectItemTerm> listAll() {
        return allInstances();
    }

    @Programmatic
    public List<ProjectItemTerm> findByProjectItem(final ProjectItem projectItem) {
        return allMatches("findByProjectItem", "projectItem", projectItem).stream().sorted().collect(Collectors.toList());
    }

    @Programmatic
    public ProjectItemTerm findUnique(final ProjectItem projectItem, final LocalDate startDate) {
        return uniqueMatch("findByProjectItemAndStartDate", "projectItem", projectItem, "startDate", startDate);
    }

    @Programmatic
    public ProjectItemTerm create(
            final ProjectItem projectItem,
            final BigDecimal amount,
            final LocalDate startDate,
            final LocalDate endDate) {

        ProjectItemTerm projectItemTerm = repositoryService.instantiate(ProjectItemTerm.class);
        projectItemTerm.setProjectItem(projectItem);
        projectItemTerm.setBudgetedAmount(amount);
        projectItemTerm.setStartDate(startDate);
        projectItemTerm.setEndDate(endDate);

        repositoryService.persist(projectItemTerm);

        return projectItemTerm;
    }

    @Programmatic
    public ProjectItemTerm findOrCreate(
            final ProjectItem projectItem,
            final BigDecimal amount,
            final LocalDate startDate,
            final LocalDate endDate) {
        ProjectItemTerm projectItemTerm = findUnique(projectItem, startDate);
        if(projectItemTerm == null) {
            projectItemTerm = create(projectItem, amount, startDate, endDate);
        }
        return projectItemTerm;
    }

    @Inject
    RepositoryService repositoryService;

}
