/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.party.dom.role;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = PartyRole.class)
public class PartyRoleRepository extends UdoDomainRepositoryAndFactory<PartyRole> {

    public PartyRoleRepository() {
        super(PartyRoleRepository.class, PartyRole.class);
    }

    public List<PartyRole> findByParty(final Party party) {
        return allMatches("findByParty",
                "party", party);
    }

    public List<PartyRole> findByRoleType(final IPartyRoleType iPartyRoleType) {
        final PartyRoleType partyRoleType = iPartyRoleType.findUsing(partyRoleTypeRepository);
        return findByRoleType(partyRoleType);
    }

    public List<PartyRole> findByRoleType(final PartyRoleType roleType) {
        return allMatches("findByRoleType",
                "roleType", roleType);
    }

    public PartyRole findByPartyAndRoleType(
            final Party party,
            final IPartyRoleType iPartyRoleType) {
        final PartyRoleType partyRoleType = iPartyRoleType.findOrCreateUsing(partyRoleTypeRepository);
        return findByPartyAndRoleType(party, partyRoleType);
    }

    public PartyRole findByPartyAndRoleType(
            final Party party,
            final PartyRoleType roleType) {
        return uniqueMatch("findByPartyAndRoleType",
                "party", party, "roleType", roleType);
    }

    public PartyRole findOrCreate(
            final Party party,
            final IPartyRoleType iPartyRoleType) {
        PartyRoleType partyRoleType = partyRoleTypeRepository.findOrCreate(iPartyRoleType);
        return findOrCreate(party, partyRoleType);
    }

    public PartyRole findOrCreate(
            final Party party,
            final PartyRoleType roleType) {
        final PartyRole partyRole = findByPartyAndRoleType(party, roleType);
        return partyRole != null ? partyRole : createPartyRole(party, roleType);
    }

    @Programmatic
    public String validateThat(final Party party, final IPartyRoleType roleType) {
        if(party == null) {
            return null;
        }
        final PartyRole roleIfAny = findByPartyAndRoleType(party, roleType);
        if (roleIfAny != null) {
            return null;
        }
        return String.format("Party does not have %s role", roleType.getTitle());
    }

    private PartyRole createPartyRole(
            final Party party,
            final PartyRoleType roleType) {
        final PartyRole partyRole = new PartyRole(party, roleType);
        persistIfNotAlready(partyRole);
        return partyRole;
    }

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}
