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

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.party.AdministrationStatus;
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

        TenantAdministrationRecord statusForTenant;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final LocalDate judicialRedressDate = new LocalDate(2020, 1, 1);
        final AdministrationStatus status = AdministrationStatus.SAFEGUARD_PLAN;
        statusForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).isEmpty();
        Assertions.assertThat(statusForTenant).isNull();

        // when
        final TenantAdministrationRecord newStatus = tenantAdministrationRecordRepository
                .upsertOrCreateNext(status, tenant, judicialRedressDate);
        statusForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(1);
        Assertions.assertThat(statusForTenant).isNotNull();
        Assertions.assertThat(statusForTenant).isEqualTo(newStatus);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(status);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate);

        // when (idempotent)
        final TenantAdministrationRecord updatedStatus = tenantAdministrationRecordRepository
                .upsertOrCreateNext(status, tenant,
                        judicialRedressDate);
        statusForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(1); // still -> idempotent
        Assertions.assertThat(statusForTenant).isNotNull();
        Assertions.assertThat(statusForTenant).isEqualTo(updatedStatus);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(status);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate);

        // when (date changes)
        final TenantAdministrationRecord updatedStatus2 = tenantAdministrationRecordRepository
                .upsertOrCreateNext(status, tenant,
                        judicialRedressDate.plusDays(1));
        statusForTenant = tenantAdministrationRecordRepository.findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(1);
        Assertions.assertThat(statusForTenant).isNotNull();
        Assertions.assertThat(statusForTenant).isEqualTo(updatedStatus2);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(status);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate.plusDays(1));

        // when
        AdministrationStatus liquidation = AdministrationStatus.LIQUIDATION;
        final TenantAdministrationRecord updatedStatus3 = tenantAdministrationRecordRepository
                .upsertOrCreateNext(liquidation, tenant,
                        judicialRedressDate);
        transactionService.nextTransaction();

        statusForTenant = tenantAdministrationRecordRepository.findUnique(tenant, liquidation);
        TenantAdministrationRecord previousStatusForTenant = tenantAdministrationRecordRepository
                .findUnique(tenant, status);

        // then
        Assertions.assertThat(tenantAdministrationRecordRepository.listAll()).hasSize(2);
        Assertions.assertThat(statusForTenant).isNotNull();
        Assertions.assertThat(statusForTenant).isEqualTo(updatedStatus3);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(liquidation);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicialRedressDate);
        Assertions.assertThat(statusForTenant.getPrevious()).isEqualTo(previousStatusForTenant);
        Assertions.assertThat(previousStatusForTenant.getNext()).isEqualTo(statusForTenant);


    }

//    @Test
//    public void mixin_works() throws Exception {
//
//        TenantAdministrationStatus statusForTenant;
//
//        // given
//        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
//        final LocalDate judicalRedressDate = new LocalDate(2020, 1, 1);
//        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).isEmpty();
//
//        // when
//        mixin(Party_changeAdministrationStatus.class, tenant).act(AdministrationStatus.SAFEGUARD_PLAN, judicalRedressDate);
//        statusForTenant = tenantAdministrationStatusRepository.findUnique(tenant);
//
//        // then
//        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).hasSize(1);
//        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(AdministrationStatus.SAFEGUARD_PLAN);
//        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicalRedressDate);
//
//    }

    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

}
