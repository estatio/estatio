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
import org.estatio.module.lease.dom.party.Party_changeAdministrationStatus;
import org.estatio.module.lease.dom.party.TenantAdministrationStatus;
import org.estatio.module.lease.dom.party.TenantAdministrationStatusRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

public class TenantAdministrationStatusRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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
    public void upsert_and_finder_works() throws Exception {

        TenantAdministrationStatus statusForTenant;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final LocalDate judicalRedressDate = new LocalDate(2020, 1, 1);
        statusForTenant = tenantAdministrationStatusRepository.findStatus(tenant);
        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).isNull();
        Assertions.assertThat(statusForTenant).isNull();

        // when
        final TenantAdministrationStatus newStatus = tenantAdministrationStatusRepository
                .upsert(AdministrationStatus.SAFEGUARD_PLAN, tenant,
                        judicalRedressDate);
        statusForTenant = tenantAdministrationStatusRepository.findStatus(tenant);

        // then
        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).hasSize(1);
        Assertions.assertThat(statusForTenant).isNotNull();
        Assertions.assertThat(statusForTenant).isEqualTo(newStatus);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(AdministrationStatus.SAFEGUARD_PLAN);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicalRedressDate);

        // when when
        final TenantAdministrationStatus updatedStatus = tenantAdministrationStatusRepository
                .upsert(AdministrationStatus.LEGAL_REDRESS, tenant,
                        judicalRedressDate.plusDays(1));
        statusForTenant = tenantAdministrationStatusRepository.findStatus(tenant);

        // then
        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).hasSize(1); // still -> idempotent
        Assertions.assertThat(statusForTenant).isNotNull();
        Assertions.assertThat(statusForTenant).isEqualTo(updatedStatus);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(AdministrationStatus.LEGAL_REDRESS);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicalRedressDate.plusDays(1));

    }

    @Test
    public void mixin_works() throws Exception {

        TenantAdministrationStatus statusForTenant;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final LocalDate judicalRedressDate = new LocalDate(2020, 1, 1);
        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).isEmpty();

        // when
        mixin(Party_changeAdministrationStatus.class, tenant).act(AdministrationStatus.SAFEGUARD_PLAN, judicalRedressDate);
        statusForTenant = tenantAdministrationStatusRepository.findStatus(tenant);

        // then
        Assertions.assertThat(tenantAdministrationStatusRepository.listAll()).hasSize(1);
        Assertions.assertThat(statusForTenant.getStatus()).isEqualTo(AdministrationStatus.SAFEGUARD_PLAN);
        Assertions.assertThat(statusForTenant.getJudicialRedressDate()).isEqualTo(judicalRedressDate);

    }

    @Inject
    TenantAdministrationStatusRepository tenantAdministrationStatusRepository;

}
