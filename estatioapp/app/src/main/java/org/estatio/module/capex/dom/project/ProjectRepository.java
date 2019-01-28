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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;


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
    public List<Project> allUnarchivedProjects() {
        return listAll().stream().filter(p->!p.isArchived()).collect(Collectors.toList());
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
            final String atPath,
            final Project parent) {

        Project project = repositoryService.instantiate(Project.class);
        project.setReference(reference);
        project.setName(name);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
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
            final String atPath,
            final Project parent) {
        Project project = findByReference(reference);
        if(project == null) {
            project = create(reference, name, startDate, endDate, atPath, parent);
        }
        return project;
    }

    public List<Project> autoComplete(final String searchPhrase) {

        final String refRegex = StringUtils.wildcardToCaseInsensitiveRegex("*".concat(searchPhrase).concat("*"));
        return allMatches("matchByReferenceOrName",
                "matcher", refRegex
        ).stream().filter(p->!p.isArchived()).collect(Collectors.toList());
    }

    public List<Project> findByFixedAsset(final FixedAsset fixedAsset){
        List<Project> result = new ArrayList<>();
        for (Project project : allUnarchivedProjects()){
            List<ProjectItem> itemsFound = project.getItems().stream().filter(x->x.getFixedAsset()==fixedAsset).collect(Collectors.toList());
            if (itemsFound.size()>0){
                result.add(project);
                continue;
            }
        }
        return result;
    }

    public List<Project> findWithoutFixedAsset(){
        List<Project> result = new ArrayList<>();
        for (Project project : allUnarchivedProjects()){
            List<ProjectItem> itemsFound = project.getItems().stream().filter(x->x.getFixedAsset()==null).collect(Collectors.toList());
            if (itemsFound.size()>0){
                result.add(project);
                continue;
            }
        }
        return result;
    }

    @Programmatic
    public List<Project> findUsingAtPath(final String atPath) {
        if (atPath==null) return Lists.emptyList();
        return allUnarchivedProjects().stream()
                .filter(p->p.getAtPath().startsWith(atPath))
                .collect(Collectors.toList());
    }

    @Programmatic
    public String generateNextProjectNumber(final String atPath) {
        final String format = atPath.startsWith("/ITA") ? "ITPR%03d" : "%04d";
        final Numerator numerator = numeratorRepository.findOrCreateNumerator(
                "Project number",
                null,
                format,
                BigInteger.ZERO,
                applicationTenancyRepository.findByPath(atPath));
        return numerator.nextIncrementStr();
    }

    @Inject
    RepositoryService repositoryService;

    @Inject NumeratorRepository numeratorRepository;

    @Inject ApplicationTenancyRepository applicationTenancyRepository;
}
