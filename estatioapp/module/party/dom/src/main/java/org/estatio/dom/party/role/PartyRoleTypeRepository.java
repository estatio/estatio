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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Party.class)
public class PartyRoleTypeRepository extends UdoDomainRepositoryAndFactory<PartyRoleType> {

    public PartyRoleTypeRepository() {
        super(PartyRoleTypeRepository.class, PartyRoleType.class);
    }

    @Programmatic
    public PartyRoleType findByKey(final String key) {
        return uniqueMatch("findByKey", "key", key);
    }

    public PartyRoleType findOrCreate(final PartyRoleTypeData partyRoleTypeData) {
        final PartyRoleType partyRoleType = findByKey(partyRoleTypeData.getKey());
        if (partyRoleType == null) {
            return create(partyRoleTypeData);
        }
        return partyRoleType;
    }

    public List<PartyRoleType> listAll() {
        return allInstances();
    }

    private PartyRoleType create(final PartyRoleTypeData partyRoleTypeData) {
        PartyRoleType partyRoleType = newTransientInstance();
        partyRoleType.setKey(partyRoleTypeData.getKey());
        partyRoleType.setTitle(partyRoleTypeData.getTitle());
        persistIfNotAlready(partyRoleType);
        return partyRoleType;
    }

}
