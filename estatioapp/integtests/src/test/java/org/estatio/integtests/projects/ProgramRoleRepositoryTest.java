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
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRepository;
import org.estatio.dom.project.ProgramRole;
import org.estatio.dom.project.ProgramRoleContributions;
import org.estatio.dom.project.ProgramRoleRepository;
import org.estatio.dom.project.ProgramRoleType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForMediaXNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.fixture.project.ProgramForGra;
import org.estatio.fixture.project.ProgramForKal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProgramRoleRepositoryTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new OrganisationForMediaXNl());
                executionContext.executeChild(this, new ProgramForKal());
                executionContext.executeChild(this, new ProgramForGra());
                executionContext.executeChild(this, new PersonForLinusTorvaldsNl());
            }
        });
    }

    @Inject
    ProgramRepository programRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    ProgramRoleRepository programRoleRepository;

    @Inject
    ProgramRoleContributions programRoleContributions;

    public static class FindRole extends ProgramRoleRepositoryTest {

        @Test
        public void withExistingProgramPartyAndRole() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Program program = programRepository.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);

            // when
            ProgramRole programActor = programRoleRepository.findRole(program, party, ProgramRoleType.PROGRAM_OWNER);

            // then
            Assert.assertNotNull(programActor);
        }

        @Test
        public void withExistingParty() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);

            // when
            Collection<ProgramRole> programActors = programRoleRepository.findRole(party);

            // then
            assertThat(programActors.size(), is(2));
        }

    }

    public static class newProgramRole extends ProgramRoleRepositoryTest {

        Party party;
        Program program;
        ProgramRole pr;

        @Test
        public void overlappingPeriod() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Program program = programRepository.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);

            // when
            pr = programRoleRepository.findRole(program, party, ProgramRoleType.PROGRAM_OWNER);
            assertThat(pr.getEndDate(), is(new LocalDate(2000, 1, 1)));

            // then
            assertThat(programRoleContributions.validateNewProgramRole(program, ProgramRoleType.PROGRAM_OWNER, party, new LocalDate(2000, 1, 1), null), is("Same party, same role, cannot have overlapping period"));
            assertNull(programRoleContributions.validateNewProgramRole(program, ProgramRoleType.PROGRAM_OWNER, party, new LocalDate(2000, 1, 2), null));
        }
    }

    public static class changeProgramRoleDates extends ProgramRoleRepositoryTest {

        Party party;
        Program program;
        ProgramRole pr1;
        ProgramRole pr2;

        @Before
        public void setUp() {
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Program program = programRepository.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);
            pr2 = programRoleRepository.createRole(program, ProgramRoleType.PROGRAM_OWNER, party, new LocalDate(2000, 1, 2), null);
        }

        @Test
        public void overlappingPeriod() throws Exception {

            // given
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            Program program = programRepository.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);

            // when
            pr1 = programRoleRepository.findRole(program, party, ProgramRoleType.PROGRAM_OWNER);
            assertThat(pr1.getEndDate(), is(new LocalDate(2000, 1, 1)));
            assertThat(pr2.getStartDate(), is(new LocalDate(2000, 1, 2)));

            // then
            assertThat(pr2.validateChangeDates(new LocalDate(2000, 1, 1), null), is("Same party, same role, cannot have overlapping period"));
            assertNull(pr2.validateChangeDates(new LocalDate(2000, 1, 3), null));
        }
    }

    public static class OnPartyRemove extends ProgramRoleRepositoryTest {

        Party oldParty;
        Party newParty;
        Program program;
        ProgramRole pr;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setUp() throws Exception {
            program = programRepository.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);
            oldParty = partyRepository.findPartyByReference(PersonForLinusTorvaldsNl.REF);
            newParty = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            pr = programRoleRepository.createRole(program, ProgramRoleType.PROGRAM_BOARDMEMBER, oldParty, new LocalDate(2000, 1, 2), null);
        }

        @Test
        public void invalidBecauseNoReplacement() throws Exception {
            // when
            Party.RemoveEvent event = new RemoveEvent();
            event.setSource(oldParty);
            event.setArguments(Lists.newArrayList());
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            programRoleRepository.on(event);

            // then
            assertTrue(event.isInvalid());
        }

        @Test
        public void executingReplacesParty() throws Exception {
            // when
            assertThat(programRoleRepository.findByParty(oldParty).size(), is(3));
            assertThat(programRoleRepository.findByParty(newParty).size(), is(2));
            Party.RemoveEvent event = new RemoveEvent();
            event.setSource(oldParty);
            event.setArguments(Lists.newArrayList(newParty));
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            programRoleRepository.on(event);
            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTING);
            programRoleRepository.on(event);

            // then
            assertThat(programRoleRepository.findByParty(oldParty).size(), is(0));
            assertThat(programRoleRepository.findByParty(newParty).size(), is(5));
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