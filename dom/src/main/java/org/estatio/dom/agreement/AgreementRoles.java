/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
public class AgreementRoles extends EstatioDomainService<AgreementRole> {

    public AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public AgreementRole newAgreementRole(final Agreement<?> agreement, final Party party, final AgreementRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        AgreementRole agreementRole = newTransientInstance();
        persistIfNotAlready(agreementRole);
        agreementRole.setStartDate(startDate);
        agreementRole.setEndDate(endDate);
        agreementRole.setType(type); // must do before associate with agreement, since part of AgreementRole#compareTo impl.
        agreementRole.setParty(party);
        agreementRole.setAgreement(agreement);
        return agreementRole;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public AgreementRole findByAgreementAndPartyAndTypeAndStartDate(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return firstMatch("findByAgreementAndPartyAndTypeAndStartDate", "agreement", agreement, "party", party, "type", type, "startDate", startDate);
    }

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public AgreementRole findByAgreementAndPartyAndTypeAndEndDate(Agreement agreement, Party party, AgreementRoleType type, LocalDate endDate) {
        return firstMatch("findByAgreementAndPartyAndTypeAndEndDate", "agreement", agreement, "party", party, "type", type, "endDate", endDate);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @NotContributed
    public AgreementRole findByAgreementAndTypeAndContainsDate(Agreement agreement, AgreementRoleType type, LocalDate date) {
        return firstMatch("findByAgreementAndTypeAndContainsDate", "agreement", agreement, "type", type, "date", date);
    }

    // //////////////////////////////////////

}