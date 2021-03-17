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

import java.math.BigDecimal;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.party.AdministrationStatus;
import org.estatio.module.lease.dom.party.TenantAdministrationLeaseDetails;
import org.estatio.module.lease.dom.party.TenantAdministrationRecord;
import org.estatio.module.lease.dom.party.TenantAdministrationRecordRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

public class TenantAdministrationRecordRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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
    public void upsert_or_create_next_works() throws Exception {

        TenantAdministrationRecord recordForTenant;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final LocalDate judicialRedressDate = new LocalDate(2020, 1, 1);
        final AdministrationStatus status = AdministrationStatus.SAFEGUARD_PLAN;
        recordForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).isEmpty();
        Assertions.assertThat(recordForTenant).isNull();

        // when
        final TenantAdministrationRecord record1 = tenantAdministrationRecordRepository
                .upsertOrCreateNext(status, tenant, judicialRedressDate);
        recordForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(1);
        Assertions.assertThat(recordForTenant).isNotNull();
        Assertions.assertThat(recordForTenant).isEqualTo(record1);
        Assertions.assertThat(recordForTenant.getStatus()).isEqualTo(status);
        Assertions.assertThat(recordForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate);

        // when (idempotent)
        final TenantAdministrationRecord updatedRecord = tenantAdministrationRecordRepository
                .upsertOrCreateNext(status, tenant,
                        judicialRedressDate);
        recordForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(1); // still -> idempotent
        Assertions.assertThat(recordForTenant).isNotNull();
        Assertions.assertThat(recordForTenant).isEqualTo(updatedRecord);
        Assertions.assertThat(recordForTenant.getStatus()).isEqualTo(status);
        Assertions.assertThat(recordForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate);

        // when (date changes)
        final TenantAdministrationRecord updatedRecord1 = tenantAdministrationRecordRepository
                .upsertOrCreateNext(status, tenant,
                        judicialRedressDate.plusDays(1));
        recordForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(1);
        Assertions.assertThat(recordForTenant).isNotNull();
        Assertions.assertThat(recordForTenant).isEqualTo(updatedRecord1);
        Assertions.assertThat(recordForTenant.getStatus()).isEqualTo(status);
        Assertions.assertThat(recordForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate.plusDays(1));

        // when
        recordForTenant.addLeaseDetails(
                Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry),
                new BigDecimal("123.45"),
                true,
                new BigDecimal("111.11"),
                true
        );
        recordForTenant.setComments("Some comments");
        transactionService.nextTransaction();
        AdministrationStatus liquidation = AdministrationStatus.LIQUIDATION;
        final TenantAdministrationRecord record2 = tenantAdministrationRecordRepository
                .upsertOrCreateNext(liquidation, tenant,
                        judicialRedressDate);
        transactionService.nextTransaction();

        recordForTenant = tenantAdministrationRecordRepository.findUnique(tenant, liquidation);
        TenantAdministrationRecord previousStatusForTenant = tenantAdministrationRecordRepository
                .findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(2);
        Assertions.assertThat(recordForTenant).isNotNull();
        Assertions.assertThat(recordForTenant).isEqualTo(record2);
        Assertions.assertThat(recordForTenant.getStatus()).isEqualTo(liquidation);
        Assertions.assertThat(recordForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate);
        Assertions.assertThat(recordForTenant.getPrevious()).isEqualTo(previousStatusForTenant);
        Assertions.assertThat(previousStatusForTenant.getNext()).isEqualTo(recordForTenant);
        Assertions.assertThat(recordForTenant.getComments()).isEqualTo(previousStatusForTenant.getComments());
        Assertions.assertThat(recordForTenant.getLeaseDetails().size()).isEqualTo(previousStatusForTenant.getLeaseDetails().size());
        final TenantAdministrationLeaseDetails copiedDetails = recordForTenant.getLeaseDetails().first();
        final TenantAdministrationLeaseDetails prevDetails = previousStatusForTenant.getLeaseDetails().first();
        Assertions.assertThat(copiedDetails.getLease()).isEqualTo(prevDetails.getLease());
        Assertions.assertThat(copiedDetails.getTenantAdministrationRecord()).isNotEqualTo(prevDetails.getTenantAdministrationRecord());
        Assertions.assertThat(copiedDetails.getDeclaredAmountOfClaim()).isEqualTo(prevDetails.getDeclaredAmountOfClaim());
        Assertions.assertThat(copiedDetails.getDebtAdmitted()).isEqualTo(prevDetails.getDebtAdmitted());
        Assertions.assertThat(copiedDetails.getAdmittedAmountOfClaim()).isEqualTo(prevDetails.getAdmittedAmountOfClaim());
        Assertions.assertThat(copiedDetails.getLeaseContinued()).isEqualTo(prevDetails.getLeaseContinued());
    }


    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

}
