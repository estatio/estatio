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

import java.util.Collection;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.Party.RemoveEvent;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRepository;
import org.estatio.dom.project.ProjectRole;
import org.estatio.dom.project.ProjectRoleContributions;
import org.estatio.dom.project.ProjectRoleRepository;
import org.estatio.dom.project.ProjectRoleType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.fixture.project.ProjectsForGra;
import org.estatio.fixture.project.ProjectsForKal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProjectRoleRepositoryTest extends EstatioIntegrationTest {

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
    ProjectRepository projectRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    ProjectRoleRepository projectRoleRepository;

    @Inject
    ProjectRoleContributions projectRoleContributions;

    public static class FindRole extends ProjectRoleRepositoryTest {

        @Test
        public void withExistingProjectPartyAndRole() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Project project = projectRepository.findProject(ProjectsForKal.PROJECT_REFERENCE).get(0);

            // when
            ProjectRole projectActor = projectRoleRepository.findRole(project, party, ProjectRoleType.PROJECT_EXECUTIVE);

            // then
            Assert.assertNotNull(projectActor);
        }

        @Test
        public void withExistingParty() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);

            // when
            Collection<ProjectRole> projectActors = projectRoleRepository.findRole(party);

            // then
            assertThat(projectActors.size(), is(3));
        }

    }

    public static class newProjectRole extends ProjectRoleRepositoryTest {

        Party party;
        Project project;
        ProjectRole pr;

        @Test
        public void overlappingPeriod() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Project project = projectRepository.findProject(ProjectsForKal.PROJECT_REFERENCE).get(0);

            // when
            pr = projectRoleRepository.findRole(project, party, ProjectRoleType.PROJECT_EXECUTIVE);
            assertThat(pr.getEndDate(), is(new LocalDate(2000, 1, 1)));
            assertThat(pr.getType(), is(ProjectRoleType.PROJECT_EXECUTIVE));
            // then
            assertThat(projectRoleContributions.validateNewProjectRole(project, ProjectRoleType.PROJECT_EXECUTIVE, party, new LocalDate(2000, 1, 1), null), is("Same party, same role, cannot have overlapping period"));
            assertNull(projectRoleContributions.validateNewProjectRole(project, ProjectRoleType.PROJECT_EXECUTIVE, party, new LocalDate(2000, 1, 2), null));
        }
    }

    public static class changeProjectRoleDates extends ProjectRoleRepositoryTest {

        Party party;
        Project project;
        ProjectRole pr1;
        ProjectRole pr2;

        @Before
        public void setUp() {
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Project project = projectRepository.findProject(ProjectsForKal.PROJECT_REFERENCE).get(0);
            pr2 = projectRoleRepository.createRole(project, ProjectRoleType.PROJECT_EXECUTIVE, party, new LocalDate(2000, 1, 2), null);
        }

        @Test
        public void overlappingPeriod() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Project project = projectRepository.findProject(ProjectsForKal.PROJECT_REFERENCE).get(0);

            // when
            pr1 = projectRoleRepository.findRole(project, party, ProjectRoleType.PROJECT_EXECUTIVE);
            assertThat(pr1.getEndDate(), is(new LocalDate(2000, 1, 1)));
            assertThat(pr2.getStartDate(), is(new LocalDate(2000, 1, 2)));

            // then
            assertThat(pr2.validateChangeDates(new LocalDate(2000, 1, 1), null), is("Same party, same role, cannot have overlapping period"));
            assertNull(pr2.validateChangeDates(new LocalDate(2000, 1, 3), null));
        }
    }

    public static class OnPartyRemove extends ProjectRoleRepositoryTest {

        Party oldParty;
        Party newParty;
        Project project;
        ProjectRole pr;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setUp() throws Exception {
            project = projectRepository.findProject(ProjectsForKal.PROJECT_REFERENCE).get(0);
            oldParty = partyRepository.findPartyByReference(PersonForLinusTorvaldsNl.REF);
            newParty = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            pr = projectRoleRepository.createRole(project, ProjectRoleType.SENIOR_SUPPLIER, oldParty, new LocalDate(2000, 1, 2), null);
        }

        @Test
        public void invalidBecauseNoReplacement() throws Exception {
            // when
            Party.RemoveEvent event = new RemoveEvent();
            event.setSource(oldParty);
            event.setArguments(Lists.newArrayList());
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            projectRoleRepository.on(event);

            // then
            assertTrue(event.isInvalid());
        }

        @Test
        public void executingReplacesParty() throws Exception {
            // when
            assertThat(projectRoleRepository.findByParty(oldParty).size(), is(4));
            assertThat(projectRoleRepository.findByParty(newParty).size(), is(3));
            Party.RemoveEvent event = new RemoveEvent();
            event.setSource(oldParty);
            event.setArguments(Lists.newArrayList(newParty));
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            projectRoleRepository.on(event);
            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTING);
            projectRoleRepository.on(event);

            // then
            assertThat(projectRoleRepository.findByParty(oldParty).size(), is(0));
            assertThat(projectRoleRepository.findByParty(newParty).size(), is(7));
        }

        @Test
        public void whenVetoingSubscriber() {
            // then
            expectedException.expect(InvalidException.class);

            // when
            wrap(oldParty).remove();
        }
    }
}