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
package org.estatio.dom.bankmandate;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Party;

@DomainService(repositoryFor = BankMandate.class)
@DomainServiceLayout(
        named = "Accounts",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "30.2")
public class BankMandates extends UdoDomainRepositoryAndFactory<BankMandate> {

    public BankMandates() {
        super(BankMandates.class, BankMandate.class);
    }

    // //////////////////////////////////////

    /**
     * for migration API only
     */
    @Programmatic
    public BankMandate newBankMandate(
            // CHECKSTYLE:OFF ParameterNumber - Wicket viewer does not support
            // aggregate value types
            @RegEx(validation = RegexValidation.REFERENCE, caseSensitive = true) final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final Party debtor,
            final Party creditor,
            final BankAccount bankAccount
            // CHECKSTYLE:ON
    ) {
        BankMandate mandate = newTransientInstance();
        mandate.setType(agreementTypeRepository.find(BankMandateConstants.AT_MANDATE));
        mandate.setReference(reference);
        mandate.setName(name);
        mandate.setStartDate(startDate);
        mandate.setEndDate(endDate);
        mandate.setBankAccount(bankAccount);

        // app tenancy derived from the debtor
        mandate.setApplicationTenancyPath(debtor.getApplicationTenancy().getPath());

        persistIfNotAlready(mandate);

        final AgreementRoleType artCreditor = agreementRoleTypeRepository.findByTitle(BankMandateConstants.ART_CREDITOR);
        mandate.newRole(artCreditor, creditor, null, null);
        final AgreementRoleType artDebtor = agreementRoleTypeRepository.findByTitle(BankMandateConstants.ART_DEBTOR);
        mandate.newRole(artDebtor, debtor, null, null);
        return mandate;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(name = "Accounts", sequence = "99")
    public List<BankMandate> allBankMandates() {
        return allInstances();
    }

    @Programmatic
    @Action(semantics = SemanticsOf.SAFE)
    public List<BankMandate> findBankMandatesFor(final BankAccount bankAccount) {
        return allMatches("findBankMandatesFor", "bankAccount", bankAccount);
    }

    // //////////////////////////////////////

    @PostConstruct
    @Programmatic
    public void init(Map<String, String> properties) {
        super.init(properties);
        AgreementType agreementType = agreementTypeRepository.findOrCreate(BankMandateConstants.AT_MANDATE);
        agreementRoleTypeRepository.findOrCreate(BankMandateConstants.ART_DEBTOR, agreementType);
        agreementRoleTypeRepository.findOrCreate(BankMandateConstants.ART_CREDITOR, agreementType);
        agreementRoleTypeRepository.findOrCreate(BankMandateConstants.ART_OWNER, agreementType);
    }

    // //////////////////////////////////////

    private AgreementTypeRepository agreementTypeRepository;

    public void injectAgreementTypes(final AgreementTypeRepository agreementTypeRepository) {
        this.agreementTypeRepository = agreementTypeRepository;
    }

    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    public void injectAgreementRoleTypes(final AgreementRoleTypeRepository agreementRoleTypeRepository) {
        this.agreementRoleTypeRepository = agreementRoleTypeRepository;
    }

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

}
