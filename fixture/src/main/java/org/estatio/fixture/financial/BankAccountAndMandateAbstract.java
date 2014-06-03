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

import java.util.List;
import javax.inject.Inject;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccounts;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandates;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.estatio.integtests.VT.ld;

public abstract class BankAccountAndMandateAbstract extends FixtureScript {

    protected BankAccountAndMandateAbstract(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected void createBankMandate(String bankAccountRef, Integer sequence, ExecutionContext executionContext) {

        final BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(bankAccountRef);

        final AgreementRoleType agreementRoleType = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);

        final Party party = bankAccount.getOwner();
        final String partyRef = party.getReference();

        final List<AgreementRole> roles = agreementRoles.findByPartyAndTypeAndContainsDate(party, agreementRoleType, ld(2013, 10, 1));
        final Lease lease = (Lease) roles.get(0).getAgreement();

        final BankMandate bankMandate = bankMandates.newBankMandate(
                partyRef + sequence.toString(),
                partyRef,
                lease.getStartDate(),
                lease.getEndDate(),
                lease.getSecondaryParty(),
                lease.getPrimaryParty(),
                bankAccount);
        executionContext.add(this, bankMandate.getReference(), bankMandate);
    }


    // //////////////////////////////////////

    @Inject
    FinancialAccounts financialAccounts;

    @Inject
    private Parties parties;

    @Inject
    private BankMandates bankMandates;

    @Inject
    private AgreementRoles agreementRoles;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;

    @Inject
    private Properties properties;

    @Inject
    private FixedAssetFinancialAccounts fixedAssetFinancialAccounts;

}
