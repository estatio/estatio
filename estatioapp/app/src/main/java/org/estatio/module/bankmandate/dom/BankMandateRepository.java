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
package org.estatio.module.bankmandate.dom;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Party;

@DomainService(
    nature = NatureOfService.DOMAIN,
    repositoryFor = BankMandate.class
)
public class BankMandateRepository extends UdoDomainRepositoryAndFactory<BankMandate> {

    public BankMandateRepository() {
        super(BankMandateRepository.class, BankMandate.class);
    }

    // //////////////////////////////////////

    public BankMandate newBankMandate(
            final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final Party debtor,
            final Party creditor,
            final BankAccount bankAccount,
            final SequenceType sequenceType,
            final Scheme scheme,
            final LocalDate signatureDate
    ) {
        BankMandate mandate = newTransientInstance();
        mandate.setType(agreementTypeRepository.find(BankMandateAgreementTypeEnum.MANDATE));
        mandate.setReference(reference);
        mandate.setName(name);
        mandate.setStartDate(startDate);
        mandate.setEndDate(endDate);
        mandate.setBankAccount(bankAccount);
        mandate.setSequenceType(sequenceType);
        mandate.setScheme(scheme);
        mandate.setSignatureDate(signatureDate);

        // app tenancy derived from the debtor
        mandate.setApplicationTenancyPath(debtor.getApplicationTenancy().getPath());

        persistIfNotAlready(mandate);

        final AgreementRoleType artCreditor = agreementRoleTypeRepository
                .find(BankMandateAgreementRoleTypeEnum.CREDITOR);
        mandate.newRole(artCreditor, creditor, null, null);
        final AgreementRoleType artDebtor = agreementRoleTypeRepository.find(
                BankMandateAgreementRoleTypeEnum.DEBTOR);
        mandate.newRole(artDebtor, debtor, null, null);
        return mandate;
    }

    // //////////////////////////////////////

    public List<BankMandate> allBankMandates() {
        return allInstances();
    }

    public List<BankMandate> findBankMandatesFor(final BankAccount bankAccount) {
        return allMatches("findBankMandatesFor", "bankAccount", bankAccount);
    }

    public BankMandate findByReference(final String reference){
        return (BankMandate) agreementRepository.findAgreementByTypeAndReference(agreementTypeRepository.find(
                BankMandateAgreementTypeEnum.MANDATE), reference);
    }

    // //////////////////////////////////////


    @Inject
    protected AgreementTypeRepository agreementTypeRepository;

    @Inject
    protected AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    protected AgreementRepository agreementRepository;

}