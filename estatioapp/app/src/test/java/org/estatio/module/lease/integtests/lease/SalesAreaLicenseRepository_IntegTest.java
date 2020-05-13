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
package org.estatio.module.lease.integtests.lease;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicenseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class SalesAreaLicenseRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, Lease_enum.KalPoison001Nl.builder());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());

            }
        });
        lease = Lease_enum.KalPoison001Nl.findUsing(serviceRegistry);
    }

    Lease lease;

    @Test
    public void salesAreaLicense_repository_integrationtest() throws Exception {

        // given
        final Occupancy occ = lease.getOccupancies().first();
        final String reference = "SomeRef";
        final String name = "SomeName";
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2020, 12, 15);
        final Party tenant = lease.getSecondaryParty();
        final Party landlord = lease.getPrimaryParty();
        final BigDecimal salesAreaNonFood = new BigDecimal("123.45");
        final BigDecimal salesAreaFood = new BigDecimal("234.56");
        final BigDecimal foodAndBeveragesArea = new BigDecimal("345.67");

        // when
        final SalesAreaLicense salesAreaLicense = salesAreaLicenseRepository.newSalesAreaLicense(
                occ,
                reference,
                name,
                startDate,
                endDate,
                tenant,
                landlord,
                salesAreaNonFood,
                salesAreaFood,
                foodAndBeveragesArea);
        transactionService.nextTransaction();

        // then
        assertThat(salesAreaLicense.getOccupancy()).isEqualTo(occ);
        assertThat(salesAreaLicense.getReference()).isEqualTo(reference);
        assertThat(salesAreaLicense.getName()).isEqualTo(name);
        assertThat(salesAreaLicense.getStartDate()).isEqualTo(startDate);
        assertThat(salesAreaLicense.getEndDate()).isEqualTo(endDate);
        assertThat(salesAreaLicense.getSalesAreaNonFood()).isEqualTo(salesAreaNonFood);
        assertThat(salesAreaLicense.getSalesAreaFood()).isEqualTo(salesAreaFood);
        assertThat(salesAreaLicense.getFoodAndBeveragesArea()).isEqualTo(foodAndBeveragesArea);
        assertThat(salesAreaLicense.getRoles()).hasSize(2);
        final AgreementRole tenantRole = salesAreaLicense.getRoles().stream()
                .filter(x -> x.getType().getKey().equals(LeaseAgreementRoleTypeEnum.TENANT.getKey())).findFirst().orElse(null);
        final AgreementRole landlordRole = salesAreaLicense.getRoles().stream()
                .filter(x -> x.getType().getKey().equals(LeaseAgreementRoleTypeEnum.LANDLORD.getKey())).findFirst().orElse(null);
        assertThat(tenantRole.getParty()).isEqualTo(tenant);
        assertThat(landlordRole.getParty()).isEqualTo(landlord);

        // and when
        final SalesAreaLicense finderResult = salesAreaLicenseRepository.findByReference(reference);
        // then
        assertThat(finderResult).isEqualTo(salesAreaLicense);

        // and when
        final List<SalesAreaLicense> finderResult2 = salesAreaLicenseRepository.findByOccupancy(occ);
        // then
        assertThat(finderResult2).hasSize(1);
        assertThat(finderResult2.get(0)).isEqualTo(salesAreaLicense);
    }

    @Test
    public void findMostRecentForOccupancy_test() throws Exception {

        // given
        final Occupancy occ = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry).getOccupancies().first();
        assertThat(salesAreaLicenseRepository.findMostRecentForOccupancy(occ)).isNotNull();
        final SalesAreaLicense firstLicense = salesAreaLicenseRepository.findMostRecentForOccupancy(occ);
        assertThat(firstLicense.getNext()).isNull();

        // when
        firstLicense.createNext(firstLicense.getStartDate(), null, null, null);
        transactionService.nextTransaction();

        // then
        assertThat(salesAreaLicenseRepository.findMostRecentForOccupancy(occ)).isNotNull();
        final SalesAreaLicense nextLicense = salesAreaLicenseRepository.findMostRecentForOccupancy(occ);
        assertThat(nextLicense.getNext()).isNull();
        assertThat(firstLicense.getNext()).isNotNull();

    }

    @Inject
    SalesAreaLicenseRepository salesAreaLicenseRepository;

}