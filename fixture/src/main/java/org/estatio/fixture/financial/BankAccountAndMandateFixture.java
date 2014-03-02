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

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccounts;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankMandates;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;

public class BankAccountAndMandateFixture extends AbstractFixture {

    @Override
    public void install() {

        createAccount("ACME", "NL31ABNA0580744433", null, "KAL");
        createAccount("HELLOWORLD", "NL31ABNA0580744434", null, "OXF");
        createAccount("TOPMODEL", "NL31ABNA0580744435", 1, null);
        createAccount("POISON", "NL31ABNA0580744437", 2, null);
        createAccount("MIRACLE", "NL31ABNA0580744439", null, null);
        createAccount("MEDIAX", "NL31ABNA0580744436", null, null);
        createAccount("PRET", "NL31ABNA0580744438", null, null);

    }

    private void createAccount(final String partyStr, final String bankAccountStr, final Integer sequence, final String propertyStr) {
        Party party = parties.findPartyByReference(partyStr);
        AgreementRoleType agreementRoleType = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);

        BankAccount bankAccount = financialAccounts.newBankAccount(party, bankAccountStr, bankAccountStr);
        if (propertyStr != null) {
            final Property property = properties.findPropertyByReference(propertyStr);
            fixedAssetFinancialAccounts.newFixedAssetFiancialAccount(property, bankAccount);
        }

        if (sequence != null) {
            List<AgreementRole> roles = agreementRoles.findByPartyAndTypeAndContainsDate(party, agreementRoleType, new LocalDate(2013, 10, 1));
            Lease lease = (Lease) roles.get(0).getAgreement();
            bankMandates.newBankMandate(
                    partyStr + sequence.toString(),
                    partyStr,
                    lease.getStartDate(),
                    lease.getEndDate(),
                    lease.getSecondaryParty(),
                    lease.getPrimaryParty(),
                    bankAccount
                    );
        }

    }

    // //////////////////////////////////////

    private FinancialAccounts financialAccounts;

    public void injectFinancialAccounts(final FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

    private Parties parties;

    public void injectParties(final Parties parties) {
        this.parties = parties;
    }

    private BankMandates bankMandates;

    public void injectBankMandates(final BankMandates bankMandates) {
        this.bankMandates = bankMandates;
    }

    private AgreementRoles agreementRoles;

    public void injectAgreementRoles(final AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    private Properties properties;

    public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

    private FixedAssetFinancialAccounts fixedAssetFinancialAccounts;

    public void injectFixedAssetFinancialAccounts(final FixedAssetFinancialAccounts fixedAssetFinancialAccounts) {
        this.fixedAssetFinancialAccounts = fixedAssetFinancialAccounts;
    }

}
