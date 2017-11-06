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
package org.estatio.capex.dom.project;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.Party;

@DomainService(repositoryFor = ProjectRole.class, nature = NatureOfService.DOMAIN)
public class ProjectRoleRepository extends UdoDomainRepositoryAndFactory<ProjectRole> {

    public ProjectRoleRepository() {
        super(ProjectRoleRepository.class, ProjectRole.class);
    }

    @Programmatic
    public List<ProjectRole> listAll() {
        return allInstances();
    }

    @Programmatic
    public ProjectRole create(
            final Project project,
            final Party party,
            final ProjectRoleTypeEnum type,
            final LocalDate startDate,
            final LocalDate endDate) {

        ProjectRole projectRole = repositoryService.instantiate(ProjectRole.class);
        projectRole.setProject(project);
        projectRole.setParty(party);
        projectRole.setType(type);
        projectRole.setStartDate(startDate);
        projectRole.setEndDate(endDate);
        repositoryService.persist(projectRole);

        return projectRole;
    }

    @Programmatic
    public List<ProjectRole> findByProject(final Project project){
        return allMatches("findByProject", "project", project);
    }

    @Programmatic
    public List<ProjectRole> findByParty(final Party party) {
        return allMatches("findByParty", "party", party);
    }


    @Inject
    RepositoryService repositoryService;
}
