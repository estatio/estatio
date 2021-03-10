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
package org.estatio.module.guarantee.fixtures.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeAgreementRoleTypeEnum;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@ToString(of={"reference"})
@Accessors(chain = true)
public class GuaranteeBuilder extends BuilderScriptAbstract<Guarantee, GuaranteeBuilder> {

    @Getter @Setter
    Lease lease;
    @Getter @Setter
    String reference;
    @Getter @Setter // optoinal
    String name;
    @Getter @Setter
    GuaranteeType guaranteeType;
    @Getter @Setter
    LocalDate startDate;
    @Getter @Setter
    LocalDate endDate;
    @Getter @Setter
    String description;
    @Getter @Setter
    BigDecimal contractualAmount;
    @Getter @Setter // optional
    BigDecimal startAmount;

    @Getter @Setter
    Organisation bank;
    @Getter @Setter //optional
    LocalDate bankRoleStartDate;
    @Getter @Setter //optional
    LocalDate bankRoleEndDate;

    @Getter
    Guarantee object;
    @Getter
    AgreementRole bankRole;

    @Override
    protected void execute(ExecutionContext ec) {

        checkParam("lease", ec, Lease.class);
        checkParam("reference", ec, String.class);
        defaultParam("name", ec, getReference());
        checkParam("guaranteeType", ec, GuaranteeType.class);
        checkParam("startDate", ec, LocalDate.class);
        checkParam("endDate", ec, LocalDate.class);
        checkParam("description", ec, String.class);
        checkParam("contractualAmount", ec, BigDecimal.class);


        final Guarantee guarantee =
                guaranteeRepository.newGuarantee(
                        lease,
                        reference, name,
                        guaranteeType,
                        startDate, endDate,
                        description,
                        contractualAmount,
                        startAmount);

        ec.addResult(this, guarantee);

        object = guarantee;

        final AgreementRoleType bankRoleType =
                agreementRoleTypeRepository.find(GuaranteeAgreementRoleTypeEnum.BANK);
        this.bankRole = guarantee.createRole(
                bankRoleType,
                bank,
                bankRoleStartDate,
                bankRoleEndDate);


    }

    // //////////////////////////////////////

    @Inject
    GuaranteeRepository guaranteeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    PartyRepository partyRepository;


}
