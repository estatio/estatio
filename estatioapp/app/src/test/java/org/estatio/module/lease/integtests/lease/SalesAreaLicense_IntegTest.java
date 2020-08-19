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

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class SalesAreaLicense_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());

            }
        });
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    Lease lease;

    @Test
    public void salesAreaLicense_integrationtest() throws Exception {

        // given
        final Occupancy occ = lease.getOccupancies().first();
        final SalesAreaLicense license = occ.getCurrentSalesAreaLicense();
        assertThat(license.getReference()).isEqualTo("OXF-TOPMODEL-001");
        assertThat(license.getName()).isEqualTo("OXF-TOPMODEL-001-SAL");
        assertThat(license.getRoles()).hasSize(2);
        final AgreementRole tenantRole = Lists.newArrayList(license.getRoles()).stream()
                .filter(x -> x.getType().getKey().equals(LeaseAgreementRoleTypeEnum.TENANT.getKey())).findFirst().orElse(null);
        final AgreementRole landlordRole = Lists.newArrayList(license.getRoles()).stream()
                .filter(x -> x.getType().getKey().equals(LeaseAgreementRoleTypeEnum.LANDLORD.getKey())).findFirst().orElse(null);

        // when
        final LocalDate startDate = new LocalDate(2020, 01, 01);
        final BigDecimal salesAreaNonFood = new BigDecimal("200.00");
        final SalesAreaLicense nextLicense = wrap(license)
                .createNext(startDate, salesAreaNonFood, null, null);
        transactionService.nextTransaction();

        // then
        assertThat(nextLicense.getOccupancy()).isEqualTo(license.getOccupancy());
        assertThat(nextLicense.getReference()).isEqualTo("OXF-TOPMODEL-001-1");
        assertThat(nextLicense.getName()).isEqualTo("OXF-TOPMODEL-001-SAL1");
        assertThat(license.getEndDate()).isEqualTo(startDate.minusDays(1));
        assertThat(nextLicense.getStartDate()).isEqualTo(startDate);
        assertThat(nextLicense.getEndDate()).isNull();
        assertThat(nextLicense.getSalesAreaNonFood()).isEqualTo(salesAreaNonFood);
        assertThat(nextLicense.getRoles()).hasSize(2);
        final AgreementRole tenantRoleNext = Lists.newArrayList(nextLicense.getRoles()).stream()
                .filter(x -> x.getType().getKey().equals(LeaseAgreementRoleTypeEnum.TENANT.getKey())).findFirst().orElse(null);
        final AgreementRole landlordRoleNext = Lists.newArrayList(nextLicense.getRoles()).stream()
                .filter(x -> x.getType().getKey().equals(LeaseAgreementRoleTypeEnum.LANDLORD.getKey())).findFirst().orElse(null);
        assertThat(tenantRole.getParty()).isEqualTo(tenantRoleNext.getParty());
        assertThat(landlordRole.getParty()).isEqualTo(landlordRoleNext.getParty());

        // and when
        wrap(occ).createNextSalesAreaLicense(startDate, BigDecimal.ZERO, null ,null);
        transactionService.nextTransaction();
        // then
        final SalesAreaLicense lastLicenseWithSameStartDate = occ.getCurrentSalesAreaLicense();
        assertThat(nextLicense.getNext()).isEqualTo(lastLicenseWithSameStartDate);
        assertThat(lastLicenseWithSameStartDate.getStartDate()).isEqualTo(startDate);
        assertThat(nextLicense.getEndDate()).isEqualTo(startDate);
        assertThat(nextLicense.getEndDate()).isEqualTo(nextLicense.getStartDate());

    }

}