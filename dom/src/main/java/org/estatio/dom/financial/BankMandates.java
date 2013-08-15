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
package org.estatio.dom.financial;

import java.util.List;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.party.Party;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;

public class BankMandates extends EstatioDomainService<BankMandate> {

    public BankMandates() {
        super(BankMandates.class, BankMandate.class);
    }

    // //////////////////////////////////////

    @NotContributed
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public BankMandate newBankMandate(
            final @Named("Reference") String reference,
            final @Named("Name") String name,
            final @Named("Start Date") LocalDate startDate,
            final @Optional @Named("End Date") LocalDate endDate,
            final @Optional @Named("Debtor") Party debtor,
            final @Optional @Named("Creditor") Party creditor, 
            final BankAccount bankAccount) {
        BankMandate mandate = newTransientInstance();
        mandate.setAgreementType(agreementTypes.find(FinancialConstants.AT_MANDATE));
        mandate.setReference(reference);
        mandate.setName(name);
        mandate.setStartDate(startDate);
        mandate.setEndDate(endDate);
        mandate.setBankAccount(bankAccount);
        persistIfNotAlready(mandate);

        final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(FinancialConstants.ART_CREDITOR);
        mandate.createInitialRole(artTenant, creditor, null, null);
        final AgreementRoleType artLandlord = agreementRoleTypes.findByTitle(FinancialConstants.ART_DEBTOR);
        mandate.createInitialRole(artLandlord, debtor, null, null);
        return mandate;
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<BankMandate> allBankMandates() {
        return allInstances();
    }

    // //////////////////////////////////////

    private AgreementTypes agreementTypes;

    public void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }
}
