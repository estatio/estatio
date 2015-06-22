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

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractInteractionEvent.Phase;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Party.RemoveEvent;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRole;
import org.estatio.dom.project.ProgramRoleType;
import org.estatio.dom.project.ProgramRoles;
import org.estatio.dom.project.ProgramRolesContributions;
import org.estatio.dom.project.Programs;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForMediaXNl;
import org.estatio.fixture.party.PersonForGinoVannelliNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.project.ProgramForGra;
import org.estatio.fixture.project.ProgramForKal;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProgramRolesTest extends EstatioIntegrationTest {
	
    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new OrganisationForMediaXNl());
                executionContext.executeChild(this, new ProgramForKal());
                executionContext.executeChild(this, new ProgramForGra());
                executionContext.executeChild(this, new PersonForGinoVannelliNl());
            }
        });
    }

    @Inject
    Programs programs;
    
    @Inject
    Parties parties;
    
    @Inject
    ProgramRoles programRoles;
    
    @Inject
    ProgramRolesContributions programRolesContributions;

    public static class FindRole extends ProgramRolesTest {

        @Test
        public void withExistingProgramPartyAndRole() throws Exception {

            // given
            Party party = parties.findPartyByReference(PersonForJohnDoeNl.REF);
            Program program = programs.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);

            // when
            ProgramRole programActor = programRoles.findRole(program, party, ProgramRoleType.PROGRAM_OWNER);

            // then
            Assert.assertNotNull(programActor);
        }
        
        @Test
        public void withExistingParty() throws Exception {

            // given
            Party party = parties.findPartyByReference(PersonForJohnDoeNl.REF);

            // when
            Collection<ProgramRole> programActors = programRoles.findRole(party);

            // then
            assertThat(programActors.size(), is(2));
        }

    }
    
    public static class newProgramRole extends ProgramRolesTest {
    	
    	Party party;
    	Program program;
    	ProgramRole pr;
    	
    	@Test
    	public void overlappingPeriod() throws Exception {
    		
    		// given
	    	Party party = parties.findPartyByReference(PersonForJohnDoeNl.REF);
			Program program = programs.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);
			
			// when
			pr = programRoles.findRole(program, party, ProgramRoleType.PROGRAM_OWNER);
			assertThat(pr.getEndDate(), is(new LocalDate(2000,1,1)));
			
			// then
			assertThat(programRolesContributions.validateNewProgramRole(program, ProgramRoleType.PROGRAM_OWNER, party, new LocalDate(2000,1,1), null), is("Same party, same role, cannot have overlapping period"));
			assertNull(programRolesContributions.validateNewProgramRole(program, ProgramRoleType.PROGRAM_OWNER, party, new LocalDate(2000,1,2), null));
    	}
    }
    
    public static class changeProgramRoleDates extends ProgramRolesTest {
    	
    	Party party;
    	Program program;
    	ProgramRole pr1;
    	ProgramRole pr2;
    	
    	@Before
    	public void setUp() {
	    	Party party = parties.findPartyByReference(PersonForJohnDoeNl.REF);
			Program program = programs.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);
    		pr2 = programRoles.createRole(program, ProgramRoleType.PROGRAM_OWNER, party, new LocalDate(2000,1,2), null);
    	}
    	
    	@Test
    	public void overlappingPeriod() throws Exception {
    		
    		// given
	    	Party party = parties.findPartyByReference(PersonForJohnDoeNl.REF);
			Program program = programs.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);
			
			// when
			pr1 = programRoles.findRole(program, party, ProgramRoleType.PROGRAM_OWNER);
			assertThat(pr1.getEndDate(), is(new LocalDate(2000,1,1)));
			assertThat(pr2.getStartDate(), is(new LocalDate(2000,1,2)));
			
			// then
			assertThat(pr2.validateChangeDates(new LocalDate(2000,1,1), null), is("Same party, same role, cannot have overlapping period"));
			assertNull(pr2.validateChangeDates(new LocalDate(2000,1,3), null));
    	}
    }
    
    public static class OnPartyRemove extends ProgramRolesTest {

        Party oldParty;
        Party newParty;
        Program program;
    	ProgramRole pr;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setUp() throws Exception {
        	program = programs.findProgram(ProgramForKal.PROGRAM_REFERENCE).get(0);
            oldParty = parties.findPartyByReference(PersonForGinoVannelliNl.REF);
            newParty = parties.findPartyByReference(PersonForJohnDoeNl.REF);
            pr = programRoles.createRole(program, ProgramRoleType.PROGRAM_BOARDMEMBER, oldParty, new LocalDate(2000,1,2), null);
        }

        @Test
        public void invalidBecauseNoReplacement() throws Exception {
            // when
            Party.RemoveEvent event = new RemoveEvent(oldParty, null, (Object[]) null);
            event.setPhase(Phase.VALIDATE);
            programRoles.on(event);

            // then
            assertTrue(event.isInvalid());
        }

        @Test
        public void executingReplacesParty() throws Exception {
            // when
        	assertThat(programRoles.findByParty(oldParty).size(), is(1));
        	assertThat(programRoles.findByParty(newParty).size(), is(2));
            Party.RemoveEvent event = new RemoveEvent(oldParty, null, newParty);
            event.setPhase(Phase.VALIDATE);
            programRoles.on(event);
            event.setPhase(Phase.EXECUTING);
            programRoles.on(event);

            // then
            assertThat(programRoles.findByParty(oldParty).size(), is(0));
            assertThat(programRoles.findByParty(newParty).size(), is(3));
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