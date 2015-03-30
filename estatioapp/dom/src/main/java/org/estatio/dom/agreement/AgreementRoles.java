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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import com.google.common.eventbus.Subscribe;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(menuOrder = "25", repositoryFor = AgreementRole.class)
@Hidden
public class AgreementRoles extends UdoDomainRepositoryAndFactory<AgreementRole> {

    public AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @NotContributed
    public AgreementRole newAgreementRole(
            final Agreement agreement,
            final Party party,
            final AgreementRoleType type,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate) {
        AgreementRole agreementRole = newTransientInstance();
        persistIfNotAlready(agreementRole);
        agreementRole.setStartDate(startDate);
        agreementRole.setEndDate(endDate);
        agreementRole.setType(type); // must do before associate with agreement,
                                     // since part of AgreementRole#compareTo
                                     // impl.
        agreementRole.setParty(party);
        agreementRole.setAgreement(agreement);
        return agreementRole;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @NotContributed
    public AgreementRole findByAgreementAndPartyAndTypeAndContainsDate(
            final Agreement agreement,
            final Party party,
            final AgreementRoleType type,
            final LocalDate date) {
        return firstMatch(
                "findByAgreementAndPartyAndTypeAndContainsDate",
                "agreement", agreement,
                "party", party,
                "type", type,
                "startDate", date,
                "endDate", LocalDateInterval.endDateFromStartDate(date));
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @NotContributed
    public AgreementRole findByAgreementAndTypeAndContainsDate(
            final Agreement agreement,
            final AgreementRoleType type,
            final LocalDate date) {
        return firstMatch(
                "findByAgreementAndTypeAndContainsDate",
                "agreement", agreement,
                "type", type,
                "startDate", date,
                "endDate", LocalDateInterval.endDateFromStartDate(date));
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @NotContributed
    public List<AgreementRole> findByParty(
            final Party party) {
        return allMatches(
                "findByParty",
                "party", party);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<AgreementRole> findByAgreement(final Agreement agreement) {
        return allMatches(
                "findByAgreement",
                "agreement", agreement);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @NotContributed
    public List<AgreementRole> findByPartyAndTypeAndContainsDate(
            final Party party,
            final AgreementRoleType type,
            final LocalDate date) {
        return allMatches(
                "findByPartyAndTypeAndContainsDate",
                "party", party,
                "type", type,
                "startDate", date,
                "endDate", LocalDateInterval.endDateFromStartDate(date));
    }

    // //////////////////////////////////////

    @Subscribe
    @Programmatic
    public void on(final Party.RemoveEvent ev) {
        Party sourceParty = (Party) ev.getSource();
        Party replacementParty = ev.getReplacement();

        List<AgreementRole> agreementRoles;
        switch (ev.getEventPhase()) {
        case VALIDATE:
            agreementRoles = findByParty(sourceParty);

            if (replacementParty == null && agreementRoles.size() > 0) {
                ev.invalidate("Party is being used in an agreement role: remove roles or provide a replacement");
            } else {
                scratchpad.put(onPartyRemoveScratchpadKey = UUID.randomUUID(), agreementRoles);
            }
            break;
        case EXECUTING:
            agreementRoles = (List<AgreementRole>) scratchpad.get(onPartyRemoveScratchpadKey);
            for (AgreementRole agreementRole : agreementRoles) {
                agreementRole.setParty(replacementParty);
            }
            break;
        default:
            break;
        }
    }


    private transient UUID onPartyRemoveScratchpadKey;

    // //////////////////////////////////////


    @Inject
    private Scratchpad scratchpad;


}