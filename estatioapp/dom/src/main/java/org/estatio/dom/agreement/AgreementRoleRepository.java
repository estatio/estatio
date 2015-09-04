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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AgreementRole.class
)
public class AgreementRoleRepository extends UdoDomainRepositoryAndFactory<AgreementRole> {

    public AgreementRoleRepository() {
        super(AgreementRoleRepository.class, AgreementRole.class);
    }

    // //////////////////////////////////////

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

    public List<AgreementRole> findByParty(
            final Party party) {
        return allMatches(
                "findByParty",
                "party", party);
    }

    // //////////////////////////////////////

    public List<AgreementRole> findByAgreement(final Agreement agreement) {
        return allMatches(
                "findByAgreement",
                "agreement", agreement);
    }

    // //////////////////////////////////////

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



}