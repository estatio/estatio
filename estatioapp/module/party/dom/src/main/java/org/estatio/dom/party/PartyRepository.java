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
package org.estatio.dom.party;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeRepository;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Party.class)
public class PartyRepository extends UdoDomainRepositoryAndFactory<Party> {

    public PartyRepository() {
        super(PartyRepository.class, Party.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Party> findParties(
            final String referenceOrName) {
        return allMatches("matchByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    @Programmatic
    public Party matchPartyByReferenceOrName(final String referenceOrName) {
        return firstMatch("matchByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    @Programmatic
    public Party findPartyByReference(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Party findPartyByReferenceOrNull(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<Party> findByRoleType(final IPartyRoleType iPartyRoleType){
        final PartyRoleType partyRoleType = iPartyRoleType.findUsing(roleTypeRepository);
        return findByRoleType(partyRoleType);
    }

    @Programmatic
    public List<Party> findByRoleType(final PartyRoleType partyRoleType){
        return allMatches("findByRoleType", "roleType", partyRoleType);
    }

    @Programmatic
    public List<Party> findByRoleTypeAndAtPath(
            final IPartyRoleType iPartyRoleType,
            final String atPath){
        PartyRoleType partyRoleType = iPartyRoleType.findOrCreateUsing(roleTypeRepository);
        return findByRoleTypeAndAtPath(partyRoleType, atPath);
    }

    @Programmatic
    public List<Party> findByRoleTypeAndAtPath(
            final PartyRoleType partyRoleType,
            final String atPath){
        List<Party> parties = findByRoleType(partyRoleType);
        parties.removeIf(x -> !Objects.equals(atPath, x.getApplicationTenancyPath()));
        return parties;
    }

    @Programmatic
    public List<Party> findByRoleTypeAndReferenceOrName(
            final IPartyRoleType iPartyRoleType,
            final String referenceOrName){
        final PartyRoleType partyRoleType = roleTypeRepository.findOrCreate(iPartyRoleType);
        return findByRoleTypeAndReferenceOrName(partyRoleType, referenceOrName);
    }

    @Programmatic
    public List<Party> findByRoleTypeAndReferenceOrName(
            final PartyRoleType partyRoleType,
            final String referenceOrName){
        return allMatches(
                "findByRoleTypeAndReferenceOrName",
                "roleType", partyRoleType,
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    // //////////////////////////////////////

    @Action(hidden = Where.EVERYWHERE)
    public List<Party> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findParties("*" + searchPhrase + "*")
                : Lists.<Party>newArrayList();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Party> allParties() {
        return allInstances();
    }

    @Programmatic
    public String validateNewParty(final String reference) {
        if (findPartyByReference(reference) != null)
            return "Reference should be unique; does similar party already exist?";
        return null;
    }

    @Inject PartyRoleTypeRepository roleTypeRepository;



}
