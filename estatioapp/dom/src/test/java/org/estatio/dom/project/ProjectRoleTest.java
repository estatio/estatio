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
package org.estatio.dom.project;

import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class ProjectRoleTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final ProjectRole pojo = new ProjectRole();
            newPojoTester()
                    .withFixture(pojos(Project.class, ProjectForTesting.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .exercise(pojo);
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<ProjectRole> {

        private Project project1;
        private Project project2;

        private Party party1;
        private Party party2;

        @Before
        public void setUp() throws Exception {
            project1 = new ProjectForTesting();
            project2 = new ProjectForTesting();
            project1.setName("A");
            project2.setName("B");
            party1 = new PartyForTesting();
            party2 = new PartyForTesting();
            party1.setName("A");
            party2.setName("B");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<ProjectRole>> orderedTuples() {
            return listOf(
                    listOf(
                            newProjectRole(null, null, null, null),
                            newProjectRole(project1, null, null, null),
                            newProjectRole(project1, null, null, null),
                            newProjectRole(project2, null, null, null)),
                    listOf(
                    		newProjectRole(project1, new LocalDate(2012,4,2), null, null),
                    		newProjectRole(project1, new LocalDate(2012,3,1), null, null),
                    		newProjectRole(project1, new LocalDate(2012,3,1), null, null),
                    		newProjectRole(project1, null, null, null))
                    ,listOf(
                    		newProjectRole(project1, new LocalDate(2012,4,2), null, null),
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_EXECUTIVE, null),
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_EXECUTIVE, null),
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_MANAGER, null))
                    ,listOf(
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_EXECUTIVE, null),
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_EXECUTIVE, party1),
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_EXECUTIVE, party1),
                    		newProjectRole(project1, new LocalDate(2012,4,2), ProjectRoleType.PROJECT_EXECUTIVE, party2))
            );
        }

        private ProjectRole newProjectRole(Project project, LocalDate startDate, ProjectRoleType type, Party party) {
            final ProjectRole pr = new ProjectRole();
            pr.setProject(project);
            pr.setParty(party);
            pr.setStartDate(startDate);
            pr.setType(type);
            return pr;
        }

    }

}