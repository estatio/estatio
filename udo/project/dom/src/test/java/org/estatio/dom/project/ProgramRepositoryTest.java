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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramRepositoryTest {

    FinderInteraction finderInteraction;

    ProgramRepository programRepository;

    @Before
    public void setup() {
        programRepository = new ProgramRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Program> allInstances() {
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



    public static class FindProgram extends ProgramRepositoryTest {

        @Test
        public void happyCase() {

        	programRepository.findProgram("some?search*Phrase");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Program.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("matchByReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("matcher")).isEqualTo((Object) "(?i)some.search.*Phrase");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class NewProgram extends ProgramRepositoryTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        private EstatioApplicationTenancyRepositoryForCountry mockEstatioApplicationTenancyRepository;

        ProgramRepository programRepository;

        @Before
        public void setup() {
            programRepository = new ProgramRepository();
            programRepository.setContainer(mockContainer);
            programRepository.estatioApplicationTenancyRepository = mockEstatioApplicationTenancyRepository;
        }


        @Test
        public void newProgram() {
            // given
            final ApplicationTenancy countryApplicationTenancy = new ApplicationTenancy();
            countryApplicationTenancy.setPath("/it");

            final Program program = new Program();
            final ApplicationTenancy programApplicationTenancy = new ApplicationTenancy();
            programApplicationTenancy.setPath("/it/REF-1");
            programApplicationTenancy.setName("REF-1 (Italy)");

            // expect
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(Program.class);
                    will(returnValue(program));

                    oneOf(mockContainer).persistIfNotAlready(program);
                }
            });

            // when
            final Program newProgram = programRepository.newProgram("REF-1", "Name-1", "Some-goal", countryApplicationTenancy);

            // then
            assertThat(newProgram.getReference()).isEqualTo("REF-1");
            assertThat(newProgram.getName()).isEqualTo("Name-1");
            assertThat(newProgram.getProgramGoal()).isEqualTo("Some-goal");
            assertThat(newProgram.getApplicationTenancyPath()).isEqualTo("/it");
        }

    }
}