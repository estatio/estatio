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
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccounts;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.BankMandates;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public abstract class BankAccountAndMandateFixture extends SimpleFixtureScript {

    protected BankAccountAndMandateFixture(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected void createBankAccountAndMandate(String partyStr, String bankAccountStr, Integer sequence, String propertyStr, ExecutionContext executionContext) {

        Party party = parties.findPartyByReference(partyStr);
        AgreementRoleType agreementRoleType = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);

        BankAccount bankAccount = financialAccounts.newBankAccount(party, bankAccountStr, bankAccountStr);
        executionContext.add(this, bankAccount.getReference(), bankAccount);
        if (propertyStr != null) {
            final Property property = properties.findPropertyByReference(propertyStr);
            fixedAssetFinancialAccounts.newFixedAssetFinancialAccount(property, bankAccount);
        }

        if (sequence != null) {
            List<AgreementRole> roles = agreementRoles.findByPartyAndTypeAndContainsDate(party, agreementRoleType, new LocalDate(2013, 10, 1));
            Lease lease = (Lease) roles.get(0).getAgreement();
            final BankMandate bankMandate = bankMandates.newBankMandate(
                    partyStr + sequence.toString(),
                    partyStr,
                    lease.getStartDate(),
                    lease.getEndDate(),
                    lease.getSecondaryParty(),
                    lease.getPrimaryParty(),
                    bankAccount
            );
            executionContext.add(this, bankMandate.getReference(), bankMandate);
        }

    }

    // //////////////////////////////////////

    @Inject
    private FinancialAccounts financialAccounts;

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
