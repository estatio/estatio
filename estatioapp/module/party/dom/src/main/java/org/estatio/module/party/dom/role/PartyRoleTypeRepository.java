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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Party.class)
public class PartyRoleTypeRepository extends UdoDomainRepositoryAndFactory<PartyRoleType> {

    public PartyRoleTypeRepository() {
        super(PartyRoleTypeRepository.class, PartyRoleType.class);
    }

    @Programmatic
    public PartyRoleType findByKey(final String key) {
        List<PartyRoleType> partyRoleTypes = listAll();
        return uniqueMatch("findByKey", "key", key);
    }

    public PartyRoleType findOrCreate(final IPartyRoleType IPartyRoleType) {
        final PartyRoleType partyRoleType = findByKey(IPartyRoleType.getKey());
        if (partyRoleType == null) {
            return create(IPartyRoleType);
        }
        return partyRoleType;
    }

    public List<PartyRoleType> listAll() {
        return allInstances();
    }

    private PartyRoleType create(final IPartyRoleType IPartyRoleType) {
        PartyRoleType partyRoleType = newTransientInstance();
        partyRoleType.setKey(IPartyRoleType.getKey());
        partyRoleType.setTitle(IPartyRoleType.getTitle());
        persistIfNotAlready(partyRoleType);
        return partyRoleType;
    }

}
