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

public class ProgramRoleTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final ProgramRole pojo = new ProgramRole();
            newPojoTester()
                    .withFixture(pojos(Program.class, ProgramForTesting.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .exercise(pojo);
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<ProgramRole> {

        private Program program1;
        private Program program2;

        private Party party1;
        private Party party2;

        @Before
        public void setUp() throws Exception {
            program1 = new ProgramForTesting();
            program2 = new ProgramForTesting();
            program1.setName("A");
            program2.setName("B");
            party1 = new PartyForTesting();
            party2 = new PartyForTesting();
            party1.setName("A");
            party2.setName("B");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<ProgramRole>> orderedTuples() {
            return listOf(
                    listOf(
                            newProgramRole(null, null, null, null),
                            newProgramRole(program1, null, null, null),
                            newProgramRole(program1, null, null, null),
                            newProgramRole(program2, null, null, null))
                    ,listOf(
                    		newProgramRole(program1, new LocalDate(2012,4,2), null, null),
                    		newProgramRole(program1, new LocalDate(2012,3,1), null, null),
                    		newProgramRole(program1, new LocalDate(2012,3,1), null, null),
                    		newProgramRole(program1, null, null, null))
                    ,listOf(
                    		newProgramRole(program1, new LocalDate(2012,4,2), null, null),
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_OWNER, null),
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_OWNER, null),
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_BOARDMEMBER, null))
                    ,listOf(
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_OWNER, null),
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_OWNER, party1),
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_OWNER, party1),
                    		newProgramRole(program1, new LocalDate(2012,4,2), ProgramRoleType.PROGRAM_OWNER, party2))
            );
        }

        private ProgramRole newProgramRole(Program program, LocalDate startDate, ProgramRoleType type, Party party) {
            final ProgramRole pr = new ProgramRole();
            pr.setProgram(program);
            pr.setParty(party);
            pr.setStartDate(startDate);
            pr.setType(type);
            return pr;
        }

    }

}