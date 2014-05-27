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
package org.estatio.fixture.agreement.refdata;


import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.financial.FinancialConstants;
import org.estatio.dom.lease.LeaseConstants;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class AgreementTypesAndRoleTypesAndCommunicationChannelTypesRefData extends FixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        create(FinancialConstants.AT_MANDATE,
                new String[]{FinancialConstants.ART_CREDITOR, FinancialConstants.ART_DEBTOR, FinancialConstants.ART_OWNER},
                new String[]{FinancialConstants.ARCCT_BAR_ADDRESS, FinancialConstants.ARCCT_FOO_ADDRESS}, fixtureResults);
        create(LeaseConstants.AT_LEASE, 
                new String[]{LeaseConstants.ART_LANDLORD, LeaseConstants.ART_MANAGER, LeaseConstants.ART_TENANT},
                new String[]{LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS, LeaseConstants.ARCCT_INVOICE_ADDRESS}, fixtureResults);
    }

    void create(final String atTitle, final String[] artTitles, final String[] arcctTitles, ExecutionContext fixtureResults) {
        AgreementType at = createAgreementType(atTitle, fixtureResults);
        for(String artTitle: artTitles) {
            createAgreementRoleType(artTitle, at, fixtureResults);
        }
        for(String arcctTitle: arcctTitles) {
            createAgreementRoleCommunicationChannelType(arcctTitle, at, fixtureResults);
        }
    }

    private AgreementType createAgreementType(final String title, ExecutionContext fixtureResults) {
        final AgreementType agreementType = getContainer().newTransientInstance(AgreementType.class);
        agreementType.setTitle(title);
        getContainer().persist(agreementType);
        return fixtureResults.add(this, agreementType.getTitle(), agreementType);
    }
    
    private AgreementRoleType createAgreementRoleType(final String title, final AgreementType appliesTo, ExecutionContext fixtureResults) {
        final AgreementRoleType agreementRoleType = getContainer().newTransientInstance(AgreementRoleType.class);
        agreementRoleType.setTitle(title);
        agreementRoleType.setAppliesTo(appliesTo);
        getContainer().persist(agreementRoleType);
        return fixtureResults.add(this, agreementRoleType.getTitle(), agreementRoleType);
    }

    private AgreementRoleCommunicationChannelType createAgreementRoleCommunicationChannelType(final String title, final AgreementType appliesTo, ExecutionContext fixtureResults) {
        final AgreementRoleCommunicationChannelType arcct = getContainer().newTransientInstance(AgreementRoleCommunicationChannelType.class);
        arcct.setTitle(title);
        arcct.setAppliesTo(appliesTo);
        getContainer().persist(arcct);
        return fixtureResults.add(this, arcct.getTitle(), arcct);
    }


}
