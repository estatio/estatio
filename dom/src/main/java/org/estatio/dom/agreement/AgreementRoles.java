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

import com.google.common.eventbus.Subscribe;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(menuOrder = "25", repositoryFor = AgreementRole.class)
@Hidden
public class AgreementRoles extends EstatioDomainService<AgreementRole> {

    public AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public AgreementRole newAgreementRole(
            final Agreement agreement,
            final Party party,
            final AgreementRoleType type,
            final @Named("Start Date") LocalDate startDate,
            final @Named("End Date") LocalDate endDate) {
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

    @ActionSemantics(Of.SAFE)
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

    @ActionSemantics(Of.SAFE)
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

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public List<AgreementRole> findByParty(
            final Party party) {
        return allMatches(
                "findByParty",
                "party", party);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
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

        switch (ev.getPhase()) {
        case VALIDATE:
            final List<AgreementRole> agreementRoles = findByParty(sourceParty);

            if (agreementRoles.size() > 0 && replacementParty == null) {
                ev.invalidate("Party is being used in an agreement role: remove roles or provide a replacement");
            }

            putAgreementRole(ev, agreementRoles);
            break;
        case EXECUTING:
            for (AgreementRole agreementRole : getAgreementRoles(ev)) {
                agreementRole.setParty(replacementParty);
            }
            break;
        default:
            break;
        }
    }

    // //////////////////////////////////////

    private static final String KEY = AgreementRole.class.getName() + ".agreementRoles";

    private static void putAgreementRole(Party.RemoveEvent ev, List<AgreementRole> communicationChannels) {
        ev.put(KEY, communicationChannels);
    }

    private static List<AgreementRole> getAgreementRoles(Party.RemoveEvent ev) {
        return (List<AgreementRole>) ev.get(KEY);
    }

}