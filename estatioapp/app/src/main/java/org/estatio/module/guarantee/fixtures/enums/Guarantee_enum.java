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
package org.estatio.module.guarantee.fixtures.enums;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.guarantee.fixtures.builders.GuaranteeBuilder;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.guarantee.dom.GuaranteeType.BANK_GUARANTEE;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Guarantee_enum
        implements PersonaWithBuilderScript<Guarantee, GuaranteeBuilder>, PersonaWithFinder<Guarantee> {

    /*
        public static final String LEASE_REFERENCE = Lease_enum.OxfTopModel001Gb.getRef();
    public static final String REFERENCE = LEASE_REFERENCE + "-D";
    public static final String PARTY_REF_BANK = Organisation_enum.DagoBankGb.getRef();

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, OrganisationAndComms_enum.DagoBankGb.builder());

        createGuaranteeForOxfTopModel001(executionContext);
    }

    private void createGuaranteeForOxfTopModel001(final ExecutionContext executionContext) {

        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REFERENCE);

        final Guarantee guarantee = newGuarantee(
                lease,
                REFERENCE,
                REFERENCE,
                GuaranteeType.BANK_GUARANTEE,
                ld(2014, 1, 1),
                ld(2015, 1, 1),
                "Description",
                bd(50000),
                executionContext);
        guarantee.createRole(
                agreementRoleTypeRepository.find(GuaranteeAgreementRoleTypeEnum.BANK),
                partyRepository.findPartyByReference(PARTY_REF_BANK),
                null,
                null);
    }

     */
    OxfTopModel001Gb(
            Lease_enum.OxfTopModel001Gb, BANK_GUARANTEE, ld(2014, 1, 1), ld(2015, 1, 1),
            "Description", bd(50000),
            OrganisationAndComms_enum.DagoBankGb
            )
    ;

    private final Lease_enum lease_d;
    private final GuaranteeType guaranteeType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description;
    private final BigDecimal contractualAmount;
    private final OrganisationAndComms_enum bank_d;

    public String getReference() {
        return lease_d.getRef() + "-D";
    }

    @Override
    public GuaranteeBuilder builder() {
        return new GuaranteeBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setReference(getReference())
                .setGuaranteeType(guaranteeType)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setDescription(description)
                .setContractualAmount(contractualAmount)
                .setPrereq((f,ec) -> f.setBank(f.objectFor(bank_d, ec)))
                ;
    }

    @Override
    public Guarantee findUsing(final ServiceRegistry2 serviceRegistry) {
        final GuaranteeRepository repo = serviceRegistry.lookupService(GuaranteeRepository.class);
        return repo.findByReference(getReference());
    }

}
