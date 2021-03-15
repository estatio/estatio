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
package org.estatio.module.lease.fixtures.bankaccount.builders;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.party.dom.Party;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"agreement","bankAccount", "sequence"}, callSuper = false)
@ToString(of={"agreement", "bankAccount", "sequence"})
@Accessors(chain = true)
public class BankMandateBuilder extends BuilderScriptAbstract<BankMandate, BankMandateBuilder> {

    @Getter @Setter
    Agreement agreement;
    @Getter @Setter
    BankAccount bankAccount;
    @Getter @Setter
    Integer sequence;
    @Getter @Setter
    private LocalDate leaseDate;

    @Getter @Setter
    SequenceType sequenceType;
    @Getter @Setter
    Scheme scheme;

    @Getter
    BankMandate object;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("agreement", ec, Agreement.class);
        checkParam("bankAccount", ec, BankAccount.class);
        checkParam("sequence", ec, Integer.class);

        // these are optional in the domain, so perhaps don't need to be mandatory
        checkParam("sequenceType", ec, Integer.class);
        checkParam("scheme", ec, Scheme.class);

        checkParam("leaseDate", ec, LocalDate.class);

        final AgreementRoleType agreementRoleType =
                agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
        final AgreementRole role =
            agreementRoleRepository.findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, leaseDate);
        final Party owner = role.getParty();

        LocalDate agreementStartDate = agreement.getStartDate();
        final BankMandate bankMandate = bankMandateRepository.newBankMandate(
                referenceFrom(owner, sequence),
                owner.getReference(),
                agreementStartDate,
                agreement.getEndDate(),
                agreement.secondaryPartyAsOfElseCurrent(agreementStartDate),
                agreement.primaryPartyAsOfElseCurrent(agreementStartDate),
                bankAccount,
                sequenceType,
                scheme,
                agreementStartDate);
        ec.addResult(this, bankMandate.getReference(), bankMandate);

        object = bankMandate;
    }

    public static String referenceFrom(final Party owner, final Integer sequence) {
        return owner.getReference() + sequence.toString();
    }

    @Inject
    BankMandateRepository bankMandateRepository;

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

}
