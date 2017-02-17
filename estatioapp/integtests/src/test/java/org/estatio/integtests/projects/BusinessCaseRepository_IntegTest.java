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

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.estatio.dom.project.BusinessCase;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRepository;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRepository;
import org.estatio.dom.project.Project_businessCase;
import org.estatio.dom.project.Project_newBusinessCase;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class BusinessCaseRepository_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new ApplicationTenancyForGlobal());
                executionContext.executeChild(this, new PropertyForKalNl());

            }
        });
    }

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ProgramRepository programRepository;

    @Inject
    ClockService clockService;

    @Inject
    ApplicationTenancies applicationTenancies;

    public static class newBusinessCase extends BusinessCaseRepository_IntegTest {

        BusinessCase bc;
        Program p1;
        Project pr1;

        private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
        private LocalDate reviewDate;
        private LocalDate now;
        private static final String AT_PATH_GLOBAL = "/";

        @Before
        public void setUp() throws Exception {

            reviewDate = clockService.now().plusDays(7);
            now = clockService.now();

            // given
            p1 = programRepository.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
            pr1 = projectRepository.newProject("PR4", "Testproject", new LocalDate(2015, 1, 1), new LocalDate(2015, 12, 31), null, null, null, p1);

            // when
            bc = mixin(Project_newBusinessCase.class, pr1).exec(BUSINESSCASE_DESCRIPTION, reviewDate);
        }

        @Test
        public void valuesSet() throws Exception {
            //then
            assertThat(mixin(Project_businessCase.class, pr1).exec(), is(bc));
            assertNull(bc.getNext());
            assertNull(bc.getPrevious());
            assertThat(bc.getDate(), is(now));
            assertThat(bc.getBusinessCaseVersion(), is(1));
            assertThat(bc.getNextReviewDate(), is(reviewDate));
            assertNull(bc.getLastUpdated());
            assertThat(bc.getDescription(), is(BUSINESSCASE_DESCRIPTION));
            assertThat(bc.getProject(), is(pr1));
            assertThat(bc.hideUpdateBusinessCase(), is(false));
            assertNull(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION, reviewDate));
            assertThat(mixin(Project_newBusinessCase.class, pr1).hideExec(), is(true));
        }

    }

    public static class newBusinessCaseValidation extends BusinessCaseRepository_IntegTest {

        BusinessCase bc;
        Program p1;
        Project pr1;

        private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
        private LocalDate wrongReviewDate;
        private static final String AT_PATH_GLOBAL = "/";

        @Before
        public void setUp() throws Exception {

            wrongReviewDate =  clockService.now().minusDays(7);

            // given
            p1 = programRepository.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
            pr1 = projectRepository.newProject("PR4", "Testproject", new LocalDate(2015, 1, 1), new LocalDate(2015, 12, 31), null, null, null, p1);

        }

        @Test
        public void valuesSet() throws Exception {

            expectedExceptions.expectMessage("A review date should not be in the past");

            // when
            bc = wrap(mixin(Project_newBusinessCase.class, pr1)).exec(BUSINESSCASE_DESCRIPTION, wrongReviewDate);

            //then
            assertThat(mixin(Project_businessCase.class, pr1).exec(), is(bc));
        }

    }

    public static class updateBusinessCase extends BusinessCaseRepository_IntegTest {

        BusinessCase bc;
        BusinessCase bc_upd;
        Program p1;
        Project pr1;

        private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
        private static final String BUSINESSCASE_DESCRIPTION_UPDATED = "This is an updated description";
        private LocalDate reviewDate;
        private LocalDate reviewDateUpdated;
        private LocalDate now;

        private static final String AT_PATH_GLOBAL = "/";

        @Before
        public void setUp() throws Exception {

            reviewDate = clockService.now().plusDays(7);
            reviewDateUpdated = clockService.now().plusDays(14);
            now = clockService.now();

            // given
            p1 = programRepository.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
            pr1 = projectRepository.newProject("PR4", "Testproject", new LocalDate(2015, 1, 1), new LocalDate(2015, 12, 31), null, null, null, p1);
            bc = mixin(Project_newBusinessCase.class, pr1).exec(BUSINESSCASE_DESCRIPTION, reviewDate);

            // when
            bc_upd = bc.updateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, reviewDateUpdated);
        }

        @Test
        public void valuesSet() throws Exception {
            //then
            assertThat(mixin(Project_businessCase.class, pr1).exec(), is(bc_upd));
            assertNull(bc_upd.getNext());
            assertThat(bc_upd.getPrevious(), is(bc));
            assertThat(bc.getNext(), is(bc_upd));
            assertNull(bc.getPrevious());
            assertThat(bc_upd.getDate(), is(now));
            assertThat(bc.getDate(), is(now));
            assertThat(bc_upd.getBusinessCaseVersion(), is(2));
            assertThat(bc.getBusinessCaseVersion(), is(1));
            assertThat(bc_upd.getNextReviewDate(), is(reviewDateUpdated));
            assertThat(bc.getNextReviewDate(), is(reviewDate));
            assertThat(bc_upd.getLastUpdated(), is(now));
            assertNull(bc.getLastUpdated());
            assertThat(bc_upd.getDescription(), is(BUSINESSCASE_DESCRIPTION_UPDATED));
            assertThat(bc.getDescription(), is(BUSINESSCASE_DESCRIPTION));
            assertThat(bc_upd.getProject(), is(pr1));
            assertThat(bc.getProject(), is(pr1));
            assertThat(bc_upd.hideUpdateBusinessCase(), is(false));
            assertThat(bc.hideUpdateBusinessCase(), is(true));
            assertNull(bc_upd.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, reviewDate));
            assertThat(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, reviewDate), is("This is no active version of the business case and cannot be updated"));
            assertThat(mixin(Project_newBusinessCase.class, pr1).hideExec(), is(true));
        }

    }

    public static class updateBusinessCaseValidation extends BusinessCaseRepository_IntegTest {

        BusinessCase bc;
        BusinessCase bc_upd;
        Program p1;
        Project pr1;

        private static final String BUSINESSCASE_DESCRIPTION = "This is a description";
        private static final String BUSINESSCASE_DESCRIPTION_UPDATED = "This is an updated description";
        private LocalDate reviewDate;
        private LocalDate wrongReviewDateUpdated;
        private static final String AT_PATH_GLOBAL = "/";

        @Before
        public void setUp() throws Exception {

            reviewDate = clockService.now().plusDays(7);
            wrongReviewDateUpdated = clockService.now().minusDays(1);

            // given
            p1 = programRepository.newProgram("TST", "TestProgram", "TestGoal", applicationTenancies.findTenancyByPath(AT_PATH_GLOBAL));
            pr1 = projectRepository.newProject("PR4", "Testproject", new LocalDate(2015, 1, 1), new LocalDate(2015, 12, 31), null, null, null, p1);
            bc = mixin(Project_newBusinessCase.class, pr1).exec(BUSINESSCASE_DESCRIPTION, reviewDate);

            // when
            bc_upd = bc.updateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, wrongReviewDateUpdated);
        }

        @Test
        public void valuesSet() throws Exception {
            //then

            assertThat(bc_upd.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, wrongReviewDateUpdated), is("A review date should not be in the past"));
            assertThat(bc.validateUpdateBusinessCase(BUSINESSCASE_DESCRIPTION_UPDATED, reviewDate), is("This is no active version of the business case and cannot be updated"));
        }

    }

}