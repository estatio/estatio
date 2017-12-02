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
package org.estatio.module.capex.integtests.project;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ProjectRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, Project_enum.KalProject1.builder());
                executionContext.executeChild(this, Project_enum.KalProject2.builder());
                executionContext.executeChild(this, Project_enum.GraProject.builder());

            }
        });
    }

    @Inject
    ProjectRepository projectRepository;

    public static class AllProjects extends ProjectRepository_IntegTest {

        @Test
        public void returnAllProjects() throws Exception {
            // when
            List<Project> allProjects = projectRepository.listAll();

            // then
            assertThat(allProjects.size(), is(3));
        }

    }

    public static class FindProjects extends ProjectRepository_IntegTest {

        @Test
        public void withReference() throws Exception {
            final List<Project> projs = projectRepository.findProject("PR1");
            assertNotNull(projs);
            assertThat(projs.size(), is(1));
        }

        @Test
        public void withName() throws Exception {
            final List<Project> projs = projectRepository.findProject("Augment parkingplace");
            assertNotNull(projs);
            assertThat(projs.size(), is(1));
        }

        @Test
        public void withWildcard() throws Exception {
            final List<Project> projs = projectRepository.findProject("Augm*");
            assertNotNull(projs);
            assertThat(projs.size(), is(1));
        }

        @Test
        public void withWildcard_returningMultiple() throws Exception {
            final List<Project> projs = projectRepository.findProject("*");
            assertNotNull(projs);
            assertThat(projs.size(), is(3));
        }
    }


}