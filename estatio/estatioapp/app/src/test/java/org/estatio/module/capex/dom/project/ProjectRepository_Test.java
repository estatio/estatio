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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectRepository_Test {

    FinderInteraction finderInteraction;

    ProjectRepository projectRepository;

    @Before
    public void setup() {
    	projectRepository = new ProjectRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Project> allInstances() {
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



    public static class FindProject extends ProjectRepository_Test {

        @Test
        public void happyCase() {

        	projectRepository.findProject("some?search*Phrase");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Project.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("matchByReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("matcher")).isEqualTo((Object) "(?i)some.search.*Phrase");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class FindByFixedAsset extends ProjectRepository_Test {

        List<Project> projectsFound;

        @Test
        public void findByFixedAsset_works() {

            // given
            Property propertyToFind = new Property();
            Property otherProperty = new Property();
            Unit someUnit = new Unit();

            Project project1 = new Project();
            ProjectItem item1_1 = new ProjectItem();
            item1_1.setProperty(propertyToFind);
            project1.getItems().add(item1_1);
            ProjectItem item1_2 = new ProjectItem();
            item1_2.setProperty(propertyToFind);
            project1.getItems().add(item1_2);
            ProjectItem item1_3 = new ProjectItem();
            item1_3.setProperty(otherProperty);
            project1.getItems().add(item1_3);

            Project project2 = new Project();
            ProjectItem item2_1 = new ProjectItem();
            item2_1.setProperty(otherProperty);
            project2.getItems().add(item2_1);

            projectRepository = new ProjectRepository(){
                @Override
                public List<Project> listAll(){
                    return Arrays.asList(project1, project2);
                }
            };

            // when
            projectsFound = projectRepository.findByFixedAsset(propertyToFind);

            // then
            assertThat(projectsFound.size()).isEqualTo(1);
            assertThat(projectsFound.get(0)).isEqualTo(project1);

            // and when
            project1.archive();
            projectsFound = projectRepository.findByFixedAsset(propertyToFind);
            // then
            assertThat(projectsFound.size()).isEqualTo(0);

            // and when
            projectsFound = projectRepository.findByFixedAsset(someUnit);

            // then
            assertThat(projectsFound.size()).isEqualTo(0);
        }

    }



    public static class FindByCountry extends ProjectRepository_Test {

        @Test
        public void findByCountry_Works() throws Exception {

            // given
            Project projectForIta = new Project();
            projectForIta.setAtPath("/ITA/SOMETHING");
            Project projectForFra = new Project();
            projectForFra.setAtPath("/FRA/SOMETHING");
            Project projectForAll = new Project();
            projectForAll.setAtPath("/");

            ProjectRepository projectRepository = new ProjectRepository(){
                @Override
                public List<Project> listAll(){
                    return Arrays.asList(projectForAll, projectForIta, projectForFra);
                }
            };

            String atPathForCountryIta = "/ITA";

            // when, then
            assertThat(projectRepository.findUsingAtPath(atPathForCountryIta)).hasSize(1);
            assertThat(projectRepository.findUsingAtPath(atPathForCountryIta)).contains(projectForIta);

            assertThat(projectRepository.findUsingAtPath(null)).hasSize(0);

            // and when
            projectForIta.archive();
            // then
            assertThat(projectRepository.findUsingAtPath(atPathForCountryIta)).isEmpty();

        }

    }

    public static class OtherTests extends ProjectRepository_Test {

        @Test
        public void autoComplete_filters_out_archived() throws Exception {

            // given
            Project project = new Project();
            String searchPhrase = "123";
            projectRepository = new ProjectRepository(){
                @Override
                protected List<Project> allMatches(final String queryName, final Object... paramArgs){
                    return Lists.newArrayList(project);
                }
            };

            // when, then
            assertThat(projectRepository.autoComplete(searchPhrase)).hasSize(1);

            // and when
            project.archive();

            // then
            assertThat(projectRepository.autoComplete(searchPhrase)).isEmpty();

        }

    }

}