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
package org.estatio.fixture.financial;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.bankmandate.*;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioFixtureScript;

import javax.inject.Inject;
import java.util.List;

import static org.estatio.integtests.VT.ld;

public abstract class BankAccountAndMandateAbstract extends EstatioFixtureScript {

    protected BankAccountAndMandateAbstract(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected void createBankMandate(String bankAccountRef, Integer sequence, SequenceType sequenceType, Scheme scheme, ExecutionContext executionContext) {

        final BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(bankAccountRef);

        final AgreementRoleType agreementRoleType = agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_TENANT);

        final Party party = bankAccount.getOwner();
        final String partyRef = party.getReference();

        final List<AgreementRole> roles = agreementRoles.findByPartyAndTypeAndContainsDate(party, agreementRoleType, ld(2013, 10, 1));
        final Lease lease = (Lease) roles.get(0).getAgreement();

        final BankMandate bankMandate = bankMandateRepository.newBankMandate(
                partyRef + sequence.toString(),
                partyRef,
                lease.getStartDate(),
                lease.getEndDate(),
                lease.getSecondaryParty(),
                lease.getPrimaryParty(),
                bankAccount,
                sequenceType,
                scheme,
                lease.getStartDate());
        executionContext.addResult(this, bankMandate.getReference(), bankMandate);
    }


    // //////////////////////////////////////

    @Inject
    FinancialAccounts financialAccounts;

    @Inject
    private Parties parties;

    @Inject
    private BankMandateMenu bankMandateMenu;
    @Inject
    private BankMandateRepository bankMandateRepository;

    @Inject
    private AgreementRoleRepository agreementRoles;

    @Inject
    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    private PropertyMenu propertyMenu;

    @Inject
    private FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
