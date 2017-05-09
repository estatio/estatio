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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectRoleRepository_Test {

    FinderInteraction finderInteraction;

    ProjectRoleRepository projectRoleRepository;

    @Before
    public void setup() {
    	projectRoleRepository = new ProjectRoleRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<ProjectRole> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }


    public static class FindByProject extends ProjectRoleRepository_Test {

        @Test
        public void happyCase() {

            final Project project = new Project();

            projectRoleRepository.findByProject(project);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(ProjectRole.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByProject");
            assertThat(finderInteraction.getArgumentsByParameterName().get("project")).isEqualTo((Object) project);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class CreateProjectRole extends ProjectRoleRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private RepositoryService repositoryService;

        @Test
        public void create_works(){
            projectRoleRepository.repositoryService = repositoryService;

            // given
            ProjectRole projectRole = new ProjectRole();
            Project project = new Project();
            Party party = new Person();
            LocalDate startDate = new LocalDate();
            LocalDate endDate = new LocalDate();

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(repositoryService).instantiate(ProjectRole.class);
                    will(returnValue(projectRole));
                    oneOf(repositoryService).persist(projectRole);
                }
            });

            // when
            ProjectRole newRole = projectRoleRepository.create(project, party, ProjectRoleType.PROJECT_MANAGER, startDate, endDate);

            // then
            assertThat(newRole.getProject()).isEqualTo(project);
            assertThat(newRole.getParty()).isEqualTo(party);
            assertThat(newRole.getType()).isEqualTo(ProjectRoleType.PROJECT_MANAGER);
            assertThat(newRole.getStartDate()).isEqualTo(startDate);
            assertThat(newRole.getEndDate()).isEqualTo(endDate);
        }

    }

}