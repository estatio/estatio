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
package org.estatio.module.lease.fixtures.bankaccount.personas;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class BankAccountAndMandateAbstract extends FixtureScript {

    protected BankAccountAndMandateAbstract(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

//    protected void createBankMandate(String ownerRef, String bankAccountRef, Integer sequence, SequenceType sequenceType, Scheme scheme, ExecutionContext executionContext) {
//        final Party owner = partyRepository.findPartyByReference(ownerRef);
//        final BankAccount bankAccount = (BankAccount) financialAccountRepository.findByOwnerAndReference(owner, bankAccountRef);
//        final AgreementRoleType agreementRoleType = agreementRoleTypeRepository.findByTitle(
//                LeaseAgreementRoleTypeEnum.TENANT.getTitle());
//        final String partyRef = owner.getReference();
//        final List<AgreementRole> roles = agreementRoles.findByPartyAndTypeAndContainsDate(owner, agreementRoleType, ld(2013, 10, 1));
//        final Lease lease = (Lease) roles.get(0).getAgreement();
//
//        final BankMandate bankMandate = bankMandateRepository.newBankMandate(
//                partyRef + sequence.toString(),
//                partyRef,
//                lease.getStartDate(),
//                lease.getEndDate(),
//                lease.getSecondaryParty(),
//                lease.getPrimaryParty(),
//                bankAccount,
//                sequenceType,
//                scheme,
//                lease.getStartDate());
//        executionContext.addResult(this, bankMandate.getReference(), bankMandate);
//    }
//
//    // //////////////////////////////////////
//
//    @Inject
//    FinancialAccountRepository financialAccountRepository;
//
//    @Inject
//    private PartyRepository partyRepository;
//
//    @Inject
//    private BankMandateRepository bankMandateRepository;
//
//    @Inject
//    private AgreementRoleRepository agreementRoles;
//
//    @Inject
//    private AgreementRoleTypeRepository agreementRoleTypeRepository;
//
//    @Inject
//    private PropertyRepository propertyRepository;
//
//    @Inject
//    private FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
