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
package org.estatio.integtests.projects;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.project.Project;
import org.estatio.dom.project.Projects;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.project.ProjectsForGra;
import org.estatio.fixture.project.ProjectsForKal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ProjectsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                
//                executionContext.executeChild(this, new OrganisationForTopModel());
                executionContext.executeChild(this, new ProjectsForKal());
                executionContext.executeChild(this, new ProjectsForGra());
                
            }
        });
    }

    @Inject
    Projects projects;

    public static class AllProjects extends ProjectsTest {

        @Test
        public void returnAllProjects() throws Exception {
            // when
            List<Project> allProjects = projects.allProjects();

            // then
            assertThat(allProjects.size(), is(3));
        }

    }

    public static class FindProjects extends ProjectsTest {

        @Test
        public void withReference() throws Exception {
            final List<Project> projs = projects.findProject("PR1");
            assertNotNull(projs);
            assertThat(projs.size(), is(1));
        }

        @Test
        public void withName() throws Exception {
            final List<Project> projs = projects.findProject("Augment parkingplace");
            assertNotNull(projs);
            assertThat(projs.size(), is(1));
        }

        @Test
        public void withWildcard() throws Exception {
            final List<Project> projs = projects.findProject("Augm*");
            assertNotNull(projs);
            assertThat(projs.size(), is(1));
        }

        @Test
        public void withWildcard_returningMultiple() throws Exception {
            final List<Project> projs = projects.findProject("*");
            assertNotNull(projs);
            assertThat(projs.size(), is(3));
        }
    }
    
    public static class updateDateOfProject extends ProjectsTest {
    	
    	@Test
    	public void rightDates() throws Exception {
    		// when
            Project project = projects.allProjects().get(0);
            
            //then
            assertNull(project.validateUpdateDates(new LocalDate(2000,01,01), new LocalDate(2000,01,01)));
    	}
    	
    	@Test
    	public void wrongDates() throws Exception {
    		// when
            Project project = projects.allProjects().get(0);
            
            //then
            assertThat(project.validateUpdateDates(new LocalDate(2000,01,02), new LocalDate(2000,01,01)), is("Start date cannot be later than End date"));
    	}
    	
    }

}