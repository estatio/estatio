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

@DomainService(repositoryFor = ProjectTerm.class, nature = NatureOfService.DOMAIN)
public class ProjectTermRepository extends UdoDomainRepositoryAndFactory<ProjectTerm> {

    public ProjectTermRepository() {
        super(ProjectTermRepository.class, ProjectTerm.class);
    }

    @Programmatic
    public List<ProjectTerm> listAll() {
        return allInstances();
    }

    @Programmatic
    public List<ProjectTerm> findProject(final Project project) {
        return allMatches("findByProject", "project", project).stream().sorted().collect(Collectors.toList());
    }

    @Programmatic
    public ProjectTerm findUnique(final Project project, final LocalDate startDate) {
        return uniqueMatch("findByProjectAndStartDate", "project", project, "startDate", startDate);
    }

    @Programmatic
    public ProjectTerm create(
            final Project project,
            final BigDecimal amount,
            final LocalDate startDate,
            final LocalDate endDate) {

        ProjectTerm budget = repositoryService.instantiate(ProjectTerm.class);
        budget.setProject(project);
        budget.setBudgetedAmount(amount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        repositoryService.persist(budget);

        return budget;
    }

    @Programmatic
    public ProjectTerm findOrCreate(
            final Project project,
            final BigDecimal amount,
            final LocalDate startDate,
            final LocalDate endDate) {
        ProjectTerm budget = findUnique(project, startDate);
        if(budget == null) {
            budget = create(project, amount, startDate, endDate);
        }
        return budget;
    }

    @Inject
    RepositoryService repositoryService;
}
