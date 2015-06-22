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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;

@DomainService(nature=NatureOfService.DOMAIN, repositoryFor = ProgramRole.class)
@DomainServiceLayout(menuOrder="10")
public class ProgramRoles extends UdoDomainRepositoryAndFactory<ProgramRole> {

    public ProgramRoles() {
        super(ProgramRoles.class, ProgramRole.class);
    }

    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProgramRole findRole(
            final Program program) {
        return firstMatch("findByProgram",
                "program", program);
    }
    
    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProgramRole findRole(
            final Program program,
            final ProgramRoleType type) {
        return firstMatch("findByProgramAndType",
                "program", program,
                "type", type);
    }
    
    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public Collection<ProgramRole> findRole(
            final Party party) {
        return allMatches("findByParty",
                "party", party);
    }

    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProgramRole findRole(
            final Program program,
            final Party party,
            final ProgramRoleType type) {
        return firstMatch("findByProgramAndPartyAndType",
                "program", program,
                "party", party,
                "type", type);
    }

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProgramRole findRole(
            final Program program,
            final Party party,
            final ProgramRoleType type,
            final LocalDate startDate,
            final LocalDate endDate) {
        return firstMatch("findByProgramAndPartyAndType",
                "program", program,
                "party", party,
                "type", type);
    }
    
	@Programmatic
	public ProgramRole createRole(
	        final Program program, 
	        final ProgramRoleType type, 
	        final Party party, 
	        final LocalDate startDate, 
	        final LocalDate endDate) {
	    final ProgramRole role = newTransientInstance(ProgramRole.class);
	    role.setStartDate(startDate);
	    role.setEndDate(endDate);
	    role.setType(type);
	    role.setParty(party);
	    role.setProgram(program);
	    persistIfNotAlready(role);
	    return role;
	}
	
	@Programmatic
    public List<ProgramRole> findByProgram(final Program program) {
        return allMatches("findByProgram", "program", program);
    }
	
	// //////////////////////////////////////
	
	@Programmatic
    public List<ProgramRole> findByParty(final Party party) {
        return allMatches("findByParty", "party", party);
    }
	
	// //////////////////////////////////////
	
    @Subscribe
    @Programmatic
    public void on(final Party.RemoveEvent ev) {
        Party sourceParty = (Party) ev.getSource();
        Party replacementParty = ev.getReplacement();

        switch (ev.getPhase()) {
        case VALIDATE:
            List<ProgramRole> programRoles = findByParty(sourceParty);

            if (programRoles.size() > 0 && replacementParty == null) {
                ev.invalidate("Party is being used in a program role: remove roles or provide a replacement");
            } else {
            	scratchpad.put(onPartyRemoveScratchpadKey = UUID.randomUUID(), programRoles);
            }
            break;
        case EXECUTING:
        	programRoles = (List<ProgramRole>) scratchpad.get(onPartyRemoveScratchpadKey);
            for (ProgramRole programRole : programRoles) {
                programRole.setParty(replacementParty);
            }
            break;
        default:
            break;
        }
    }
    
    private transient UUID onPartyRemoveScratchpadKey;
    
    @Inject
    private Scratchpad scratchpad;
    
    // //////////////////////////////////////

}




