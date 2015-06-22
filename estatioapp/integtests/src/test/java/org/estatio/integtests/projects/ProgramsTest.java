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

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.project.Program;
import org.estatio.dom.project.Programs;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForMediaXNl;
import org.estatio.fixture.project.ProgramForGra;
import org.estatio.fixture.project.ProgramForKal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ProgramsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForMediaXNl());
                executionContext.executeChild(this, new ProgramForGra());
                executionContext.executeChild(this, new ProgramForKal());
            }
        });
    }

    @Inject
    Programs programs;

    public static class AllPrograms extends ProgramsTest {

        @Test
        public void returnAllPrograms() throws Exception {
            // when
            List<Program> allPrograms = programs.allPrograms();

            // then
            assertThat(allPrograms.size(), is(2));
        }

    }

    public static class FindPrograms extends ProgramsTest {

        @Test
        public void withReference() throws Exception {
            final List<Program> progs = programs.findProgram("KAL_P1");
            assertNotNull(progs);
            assertThat(progs.size(), is(1));
        }

        @Test
        public void withName() throws Exception {
            final List<Program> progs = programs.findProgram("Program 1");
            assertNotNull(progs);
            assertThat(progs.size(), is(1));
        }

        @Test
        public void withWildcard() throws Exception {
            final List<Program> progs = programs.findProgram("Pro*");
            assertNotNull(progs);
            assertThat(progs.size(), is(1));
        }

        @Test
        public void withWildcard_returningMultiple() throws Exception {
            final List<Program> progs = programs.findProgram("*");
            assertNotNull(progs);
            assertThat(progs.size(), is(2));
        }
    }

}