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

import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.party.dom.Party;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"bankAcount", "sequence"}, callSuper = false)
@ToString(of={"bankAcount", "sequence"})
@Accessors(chain = true)
public class BankMandateBuilder extends BuilderScriptAbstract<BankMandate, BankMandateBuilder> {


    @Getter @Setter
    Lease lease;
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

        checkParam("lease", ec, Lease.class);
        checkParam("bankAccount", ec, BankAccount.class);
        checkParam("sequence", ec, Integer.class);

        // these are optional in the domain, so perhaps don't need to be mandatory
        checkParam("sequenceType", ec, Integer.class);
        checkParam("scheme", ec, Scheme.class);

        checkParam("leaseDate", ec, LocalDate.class);

        final AgreementRoleType agreementRoleType =
                agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
        final AgreementRole role =
            agreementRoleRepository.findByAgreementAndTypeAndContainsDate(lease, agreementRoleType, leaseDate);
        final Party owner = role.getParty();

        final BankMandate bankMandate = bankMandateRepository.newBankMandate(
                referenceFrom(owner, sequence),
                owner.getReference(),
                lease.getStartDate(),
                lease.getEndDate(),
                lease.getSecondaryParty(),
                lease.getPrimaryParty(),
                bankAccount,
                sequenceType,
                scheme,
                lease.getStartDate());
        ec.addResult(this, bankMandate.getReference(), bankMandate);
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
