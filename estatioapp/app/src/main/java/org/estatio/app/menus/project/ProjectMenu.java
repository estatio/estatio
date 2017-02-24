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

package org.estatio.app.menus.project;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.project.ProjectMenu"
)
@DomainServiceLayout(
        menuOrder = "35",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Projects"
)
public class ProjectMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public List<Project> allProjects() {
        return projectRepository.allProjects();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Project> findProject(final @ParameterLayout(named = "Name or reference") String searchStr) {
        return projectRepository.findProject(searchStr);
    }

    @Inject
    ProjectRepository projectRepository;
}
