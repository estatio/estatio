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
package org.estatio.dom.project;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = Project.class, nature = NatureOfService.DOMAIN)
public class ProjectRepository extends UdoDomainRepositoryAndFactory<Project> {

    public ProjectRepository() {
        super(ProjectRepository.class, Project.class);
    }

    @Programmatic
    public List<Project> listAll() {
        return allInstances();
    }

    @Programmatic
    public List<Project> findProject(String searchStr) {
        return allMatches("matchByReferenceOrName", "matcher", StringUtils.wildcardToCaseInsensitiveRegex(searchStr));
    }

    @Programmatic
    public Project findByReference(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Project create(
            final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal budgetedAmount,
            final String atPath,
            final Project parent) {

        Project project = repositoryService.instantiate(Project.class);
        project.setReference(reference);
        project.setName(name);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setBudgetedAmount(budgetedAmount);
        project.setAtPath(atPath);
        project.setParent(parent);

        repositoryService.persist(project);

        return project;
    }

    @Programmatic
    public Project findOrCreate(
            final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal budgetedAmount,
            final String atPath,
            final Project parent) {
        Project project = findByReference(reference);
        if(project == null) {
            project = create(reference, name, startDate, endDate, budgetedAmount, atPath, parent);
        }
        return project;
    }

    @Inject
    RepositoryService repositoryService;
}
