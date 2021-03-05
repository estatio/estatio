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
package org.estatio.module.lease.integtests.party;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.assertj.core.api.Assertions;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.party.*;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

public class TenantAdministrationLeaseDetailsRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void upsert_works() throws Exception {

        TenantAdministrationLeaseDetails leaseDetails;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final TenantAdministrationRecord record = tenantAdministrationRecordRepository
                .upsertOrCreateNext(AdministrationStatus.SAFEGUARD_PLAN, tenant, new LocalDate(2020, 1, 1));
        Assertions.assertThat(record).isNotNull();
        final BigDecimal declaredAmountOfClaim = new BigDecimal("1000.00");
        leaseDetails = tenantAdministrationLeaseDetailsRepository.findUnique(record, lease);
        Assertions.assertThat(tenantAdministrationLeaseDetailsRepository.listAll()).isEmpty();
        Assertions.assertThat(leaseDetails).isNull();

        // when
        final TenantAdministrationLeaseDetails newLeaseDetails = tenantAdministrationLeaseDetailsRepository.upsert(
                record,
                lease,
                declaredAmountOfClaim,
                null,
                null,
                null
        );
        leaseDetails = tenantAdministrationLeaseDetailsRepository.findUnique(record, lease);

        // then
        Assertions.assertThat(tenantAdministrationLeaseDetailsRepository.listAll()).hasSize(1);
        Assertions.assertThat(leaseDetails).isNotNull();
        Assertions.assertThat(leaseDetails).isEqualTo(newLeaseDetails);
        Assertions.assertThat(leaseDetails.getLease()).isEqualTo(lease);
        Assertions.assertThat(leaseDetails.getTenantAdministrationRecord()).isEqualTo(record);
        Assertions.assertThat(leaseDetails.getDeclaredAmountOfClaim()).isEqualTo(declaredAmountOfClaim);
        Assertions.assertThat(leaseDetails.getAdmittedAmountOfClaim()).isNull();
        Assertions.assertThat(leaseDetails.getDebtAdmitted()).isNull();
        Assertions.assertThat(leaseDetails.getLeaseContinued()).isNull();

        // and when
        BigDecimal admittedAmountOfClaim = new BigDecimal("800.00");
        final TenantAdministrationLeaseDetails newLeaseDetails2 = tenantAdministrationLeaseDetailsRepository.upsert(
                record,
                lease,
                declaredAmountOfClaim,
                true,
                admittedAmountOfClaim,
                true
        );
        leaseDetails = tenantAdministrationLeaseDetailsRepository.findUnique(record, lease);

        // then
        Assertions.assertThat(tenantAdministrationLeaseDetailsRepository.listAll()).hasSize(1);
        Assertions.assertThat(leaseDetails).isNotNull();
        Assertions.assertThat(leaseDetails).isEqualTo(newLeaseDetails2);
        Assertions.assertThat(leaseDetails.getLease()).isEqualTo(lease);
        Assertions.assertThat(leaseDetails.getTenantAdministrationRecord()).isEqualTo(record);
        Assertions.assertThat(leaseDetails.getDeclaredAmountOfClaim()).isEqualTo(declaredAmountOfClaim);
        Assertions.assertThat(leaseDetails.getAdmittedAmountOfClaim()).isEqualTo(admittedAmountOfClaim);
        Assertions.assertThat(leaseDetails.getDebtAdmitted()).isTrue();
        Assertions.assertThat(leaseDetails.getLeaseContinued()).isTrue();

    }

    @Inject
    TenantAdministrationLeaseDetailsRepository tenantAdministrationLeaseDetailsRepository;

    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

}
