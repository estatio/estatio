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

public class BankAccountAndMandateFixture extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        createAccount("ACME", "NL31ABNA0580744433", null, "KAL", executionContext);
        createAccount("HELLOWORLD", "NL31ABNA0580744434", null, "OXF", executionContext);
        createAccount("TOPMODEL", "NL31ABNA0580744435", 1, null, executionContext);
        createAccount("POISON", "NL31ABNA0580744437", 2, null, executionContext);
        createAccount("MIRACLE", "NL31ABNA0580744439", null, null, executionContext);
        createAccount("MEDIAX", "NL31ABNA0580744436", null, null, executionContext);
        createAccount("PRET", "NL31ABNA0580744438", null, null, executionContext);

    }

    private void createAccount(
            final String partyStr,
            final String bankAccountStr,
            final Integer sequence,
            final String propertyStr,
            final ExecutionContext executionContext) {

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
