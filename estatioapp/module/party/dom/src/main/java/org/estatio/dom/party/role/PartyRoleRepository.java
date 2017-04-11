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
package org.estatio.dom.party.role;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Party.class)
public class PartyRoleRepository extends UdoDomainRepositoryAndFactory<PartyRole> {

    public PartyRoleRepository() {
        super(PartyRoleRepository.class, PartyRole.class);
    }

    public List<PartyRole> findbyParty(
            final Party party) {
        return allMatches("findByParty",
                "party", party);
    }

    public List<PartyRole> findbyRoleType(
            final PartyRoleType roleType) {
        return allMatches("findByRoleType",
                "roleType", roleType);
    }

    public PartyRole findbyPartyAndRoleType(
            final Party party, final PartyRoleType roleType) {
        return uniqueMatch("findByPartyAndRoleType",
                "party", party, "roleType", roleType);
    }

    public PartyRole findOrCreate(final Party party, final PartyRoleType roleType) {
        final PartyRole partyRole = findbyPartyAndRoleType(party, roleType);
        return partyRole != null ? partyRole : createPartyRole(party, roleType);
    }

    public PartyRole findOrCreate(final Party party, final PartyRoleTypeData roleTypeData) {
        return findOrCreate(party, roleTypeData.findUsing(partyRoleTypeRepository));
    }

    private PartyRole createPartyRole(final Party party, final PartyRoleType roleType) {
        PartyRole partyRole = newTransientInstance();
        partyRole.setParty(party);
        partyRole.setRoleType(roleType);
        persistIfNotAlready(partyRole);
        return partyRole;
    }

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}
