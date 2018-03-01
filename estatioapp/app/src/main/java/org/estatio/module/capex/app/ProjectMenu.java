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

package org.estatio.module.capex.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.capex.dom.project.ProjectMenu"
)
@DomainServiceLayout(
        named = "Projects",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "55.1"
)
public class ProjectMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public List<Project> allProjects() {
        return projectRepository.listAll();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Project> findProject(final @ParameterLayout(named = "Name or reference") String searchStr) {
        return projectRepository.findProject(searchStr);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Project newProject(
            final String reference,
            final String name,
            final @Nullable LocalDate startDate,
            final @Nullable LocalDate endDate,
            final ApplicationTenancy applicationTenancy,
            final @Nullable Project parent) {
        return projectRepository.create(reference, name, startDate, endDate, applicationTenancy.getPath(), parent);
    }

    public ApplicationTenancy default4NewProject() {
        final String usersAtPath = meService.me().getAtPath();
        // can't use ApplicationTenancyRepository#findByPath because that uses uniqueMatch, and there might not be any match.
        return repositoryService.firstMatch(
                new QueryDefault<>(ApplicationTenancy.class, "findByPath", "path", usersAtPath));
    }

    public List<ApplicationTenancy> choices4NewProject() {
        return applicationTenancyRepository.allTenancies();
    }

    public List<Project> choices5NewProject(){
        // TODO: (ECP-438) until we find out more about the process, prevent a the choice of a project having items
        return projectRepository.listAll().stream().filter(x->x.getItems().isEmpty()).collect(Collectors.toList());
    }

    @Inject
    ProjectRepository projectRepository;
    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    RepositoryService repositoryService;
    @Inject
    MeService meService;
}
