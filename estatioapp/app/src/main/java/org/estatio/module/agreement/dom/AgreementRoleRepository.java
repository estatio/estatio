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
package org.estatio.module.agreement.dom;

import java.util.List;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.estatio.module.index.dom.Index;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.party.dom.Party;

import javax.inject.Inject;

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
            final LocalDate startDate,
            final LocalDate endDate) {
        AgreementRole agreementRole = factoryService.instantiate(AgreementRole.class);
        repositoryService.persistAndFlush(agreementRole);
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
        final LocalDate queryDate = date == null ? new LocalDate(1980, 1, 1) : date;
        List<AgreementRole> list = repositoryService.allMatches(new QueryDefault<>(AgreementRole.class,
                "findByAgreementAndPartyAndTypeAndContainsDate",
                "agreement", agreement,
                "party", party,
                "type", type,
                "startDate", queryDate,
                "endDate", LocalDateInterval.endDateFromStartDate(queryDate)));
        return list.isEmpty() ? null : list.get(0);
    }

    // //////////////////////////////////////

    public AgreementRole findByAgreementAndTypeAndContainsDate(
            final Agreement agreement,
            final AgreementRoleType type,
            final LocalDate date) {
        final LocalDate queryDate = date == null ? new LocalDate(1980, 1, 1) : date;
        List<AgreementRole> list = repositoryService.allMatches(new QueryDefault<>(AgreementRole.class,
                "findByAgreementAndTypeAndContainsDate",
                "agreement", agreement,
                "type", type,
                "startDate", queryDate,
                "endDate", LocalDateInterval.endDateFromStartDate(queryDate)));
        return list.isEmpty() ? null : list.get(0);
    }

    // //////////////////////////////////////

    public List<AgreementRole> findByParty(
            final Party party) {
        return repositoryService.allMatches(new QueryDefault<>(AgreementRole.class,
                "findByParty",
                "party", party));
    }

    // //////////////////////////////////////

    public List<AgreementRole> findByAgreement(final Agreement agreement) {
        return repositoryService.allMatches(new QueryDefault<>(AgreementRole.class,
                "findByAgreement",
                "agreement", agreement));
    }

    // //////////////////////////////////////

    public List<AgreementRole> findByPartyAndTypeAndContainsDate(
            final Party party,
            final AgreementRoleType type,
            final LocalDate date) {
        final LocalDate queryDate = date == null ? new LocalDate(1980, 1, 1) : date;
        return repositoryService.allMatches(new QueryDefault<>(AgreementRole.class,
                "findByPartyAndTypeAndContainsDate",
                "party", party,
                "type", type,
                "startDate", queryDate,
                "endDate", LocalDateInterval.endDateFromStartDate(queryDate)));
    }

    public List<AgreementRole> findByPartyAndType(
            final Party party,
            final AgreementRoleType type) {
        return repositoryService.allMatches(new QueryDefault<>(AgreementRole.class,
                "findByPartyAndType",
                "party", party,
                "type", type));
    }

    @javax.inject.Inject
    public RepositoryService repositoryService;

}