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
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.imports.ProjectImportManager;
import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;

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

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Project> allProjects() {
        return projectRepository.listAll();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Project> findProject(final @ParameterLayout(named = "Name or reference") String searchStr, final boolean includeArchived) {
        return includeArchived ?
                projectRepository.findProject(searchStr) :
                projectRepository.findProject(searchStr)
                        .stream()
                        .filter(p->!p.isArchived())
                        .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Project newProject(
            final @Parameter(maxLength = ReferenceType.Meta.MAX_LEN) String reference,
            final String name,
            final @Nullable LocalDate startDate,
            final @Nullable LocalDate endDate,
            final ApplicationTenancy applicationTenancy,
            final @Nullable Project parent) {
        return projectRepository.create(reference, name, startDate, endDate, applicationTenancy.getPath(), parent);
    }

    public ApplicationTenancy default4NewProject() {
        final String usersAtPath = meService.me().getFirstAtPathUsingSeparator(';');
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

    public boolean hideNewProject() {
        return !hideNewProjectItaly();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public Project newProjectItaly(
            final String name,
            final @Nullable LocalDate startDate,
            final @Nullable LocalDate endDate) {
        String atPath = "/ITA";
        String reference = projectRepository.generateNextProjectNumber(atPath);
        return projectRepository.create(reference, name, startDate, endDate, atPath, null);
    }

    public boolean hideNewProjectItaly() {
        final String atPath = meService.me().getAtPath();
        final boolean italianUser = atPath != null && atPath.startsWith("/ITA");
        return !italianUser;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Project> projectsToBeReviewed(){
        return projectRepository.findReviewDateInInterval(LocalDateInterval.including(clockService.now().minusYears(1), clockService.now().plusMonths(1)));
    }


    @Action(semantics = SemanticsOf.SAFE)
    public ProjectImportManager importProjects(final Country country, @Nullable final Project project){
        return new ProjectImportManager(country, project);
    }

    public Country default0ImportProjects(){
        return countryServiceForCurrentUser.countriesForCurrentUser().isEmpty() ? null : countryServiceForCurrentUser.countriesForCurrentUser().get(0);
    }

    public List<Country> choices0ImportProjects(){
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public List<Project> choices1ImportProjects(final Country country, @Nullable final Project project){
        return projectRepository.findUsingAtPath(deriveAtPathFromCountry(country));
    }

    private String deriveAtPathFromCountry(final Country country){
        return "/" + country.getReference();
    }

    @Inject
    ProjectRepository projectRepository;
    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    RepositoryService repositoryService;
    @Inject
    MeService meService;
    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;
    @Inject ClockService clockService;
}
