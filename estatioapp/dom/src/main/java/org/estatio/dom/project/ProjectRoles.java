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

@DomainService(nature=NatureOfService.DOMAIN, repositoryFor = ProjectRole.class)
@DomainServiceLayout(menuOrder="10")
public class ProjectRoles extends UdoDomainRepositoryAndFactory<ProjectRole> {

    public ProjectRoles() {
        super(ProjectRoles.class, ProjectRole.class);
    }

    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProjectRole findRole(
            final Project project) {
        return firstMatch("findByProject",
                "project", project);
    }

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProjectRole findRole(
            final Project project,
            final ProjectRoleType type) {
        return firstMatch("findByProjectAndType",
                "project", project,
                "type", type);
    }
    
    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public Collection<ProjectRole> findRole(
            final Party party) {
        return allMatches("findByParty",
                "party", party);
    }

    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProjectRole findRole(
            final Project project,
            final Party party,
            final ProjectRoleType type) {
        return firstMatch("findByProjectAndPartyAndType",
                "project", project,
                "party", party,
                "type", type);
    }

    @Action(semantics=SemanticsOf.SAFE)
    @ActionLayout(contributed=Contributed.AS_NEITHER)
    public ProjectRole findRole(
            final Project project,
            final Party party,
            final ProjectRoleType type,
            final LocalDate startDate,
            final LocalDate endDate) {
        return firstMatch("findByProjectAndPartyAndType",
                "project", project,
                "party", party,
                "type", type);
    }
    
    // //////////////////////////////////////
    
    @Programmatic
    public ProjectRole createRole(
    		final Project project,
            final ProjectRoleType type, 
            final Party party, 
            final LocalDate startDate, 
            final LocalDate endDate) {
        final ProjectRole role = newTransientInstance(ProjectRole.class);
        role.setStartDate(startDate);
        role.setEndDate(endDate);
        role.setType(type);
        role.setParty(party);
        role.setProject(project);

        persistIfNotAlready(role);

        return role;
    }
    
    // //////////////////////////////////////

	@Programmatic
    public List<ProjectRole> findByProject(final Project project) {
        return allMatches("findByProject", "project", project);
    }
	
	// //////////////////////////////////////
	
	@Programmatic
    public List<ProjectRole> findByParty(final Party party) {
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
            List<ProjectRole> projectRoles = findByParty(sourceParty);

            if (projectRoles.size() > 0 && replacementParty == null) {
                ev.invalidate("Party is being used in a project role: remove roles or provide a replacement");
            } else {
            	scratchpad.put(onPartyRemoveScratchpadKey = UUID.randomUUID(), projectRoles);
            }
            break;
        case EXECUTING:
        	projectRoles = (List<ProjectRole>) scratchpad.get(onPartyRemoveScratchpadKey);
            for (ProjectRole projectRole : projectRoles) {
                projectRole.setParty(replacementParty);
            }
            break;
        default:
            break;
        }
    }
    
    private transient UUID onPartyRemoveScratchpadKey;
    
    @Inject
    private Scratchpad scratchpad;
    
}
