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
import org.estatio.module.lease.dom.party.*;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

public class ContinuationPlanRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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
    public void find_or_create_works() throws Exception {

        ContinuationPlan continuationPlan;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final TenantAdministrationRecord record = tenantAdministrationRecordRepository
                .upsertOrCreateNext(AdministrationStatus.SAFEGUARD_PLAN, tenant, new LocalDate(2020, 1, 1)); // TODO: make fixture?
        Assertions.assertThat(record).isNotNull();
        final LocalDate judgmentDate = new LocalDate(2021, 1, 1);
        continuationPlan = continuationPlanRepository.findUnique(record);
        Assertions.assertThat(continuationPlanRepository.listAll()).isEmpty();
        Assertions.assertThat(continuationPlan).isNull();

        // when
        final ContinuationPlan newContinuationPlan = continuationPlanRepository.findOrCreate(record, judgmentDate);
        continuationPlan = continuationPlanRepository.findUnique(record);

        // then
        Assertions.assertThat(continuationPlanRepository.listAll()).hasSize(1);
        Assertions.assertThat(continuationPlan).isNotNull();
        Assertions.assertThat(continuationPlan).isEqualTo(newContinuationPlan);
        Assertions.assertThat(continuationPlan.getTenantAdministrationRecord()).isEqualTo(record);
        Assertions.assertThat(continuationPlan.getJudgmentDate()).isEqualTo(judgmentDate);
        Assertions.assertThat(continuationPlan.getEntries()).isEmpty();

        // and when (already exists)
        final ContinuationPlan newContinuationPlan2 = continuationPlanRepository.findOrCreate(record, null);
        continuationPlan = continuationPlanRepository.findUnique(record);

        // then
        Assertions.assertThat(continuationPlanRepository.listAll()).hasSize(1);
        Assertions.assertThat(continuationPlan).isNotNull();
        Assertions.assertThat(continuationPlan).isEqualTo(newContinuationPlan);
        Assertions.assertThat(continuationPlan).isEqualTo(newContinuationPlan2);
        Assertions.assertThat(continuationPlan.getTenantAdministrationRecord()).isEqualTo(record);
        Assertions.assertThat(continuationPlan.getJudgmentDate()).isEqualTo(judgmentDate);
        Assertions.assertThat(continuationPlan.getEntries()).isEmpty();

    }


    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

    @Inject
    ContinuationPlanRepository continuationPlanRepository;

}
