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
import java.math.BigDecimal;

public class ContinuationPlanEntryRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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

        ContinuationPlanEntry continuationPlanEntry;

        // given
        final Organisation tenant = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
        final TenantAdministrationRecord record = tenantAdministrationRecordRepository
                .upsertOrCreateNext(AdministrationStatus.SAFEGUARD_PLAN, tenant, new LocalDate(2020, 1, 1)); // TODO: make fixture?
        Assertions.assertThat(record).isNotNull();
        final ContinuationPlan continuationPlan = record.createContinuationPlan(new LocalDate(2020, 6, 1));
        Assertions.assertThat(continuationPlan).isNotNull();
        final LocalDate date = new LocalDate(2021, 1, 1);
        final BigDecimal percentage = new BigDecimal("5.00");
        continuationPlanEntry = continuationPlanEntryRepository.findUnique(continuationPlan, date);
        Assertions.assertThat(continuationPlanEntryRepository.listAll()).isEmpty();
        Assertions.assertThat(continuationPlanEntry).isNull();

        // when
        final ContinuationPlanEntry newContinuationPlanEntry = continuationPlanEntryRepository.upsert(continuationPlan, date, percentage);
        continuationPlanEntry = continuationPlanEntryRepository.findUnique(continuationPlan, date);

        // then
        Assertions.assertThat(continuationPlanEntryRepository.listAll()).hasSize(1);
        Assertions.assertThat(continuationPlanEntry).isNotNull();
        Assertions.assertThat(continuationPlanEntry).isEqualTo(newContinuationPlanEntry);
        Assertions.assertThat(continuationPlanEntry.getContinuationPlan()).isEqualTo(continuationPlan);
        Assertions.assertThat(continuationPlanEntry.getDate()).isEqualTo(date);
        Assertions.assertThat(continuationPlanEntry.getPercentage()).isEqualTo(percentage);
        Assertions.assertThat(continuationPlanEntry.getEntryValues()).isEmpty();

        // and when (new percentage)
        final BigDecimal newPercentage = new BigDecimal("8.00");
        final ContinuationPlanEntry newContinuationPlanEntry2 = continuationPlanEntryRepository.upsert(continuationPlan, date, newPercentage);
        continuationPlanEntry = continuationPlanEntryRepository.findUnique(continuationPlan, date);

        // then
        Assertions.assertThat(continuationPlanEntryRepository.listAll()).hasSize(1);
        Assertions.assertThat(continuationPlanEntry).isNotNull();
        Assertions.assertThat(continuationPlanEntry).isEqualTo(newContinuationPlanEntry2);
        Assertions.assertThat(continuationPlanEntry.getContinuationPlan()).isEqualTo(continuationPlan);
        Assertions.assertThat(continuationPlanEntry.getDate()).isEqualTo(date);
        Assertions.assertThat(continuationPlanEntry.getPercentage()).isEqualTo(newPercentage);
        Assertions.assertThat(continuationPlanEntry.getEntryValues()).isEmpty();

        // and when (new date)
        final LocalDate newDate = new LocalDate(2021, 1, 31);
        final ContinuationPlanEntry newContinuationPlanEntry3 = continuationPlanEntryRepository.upsert(continuationPlan, newDate, newPercentage);
        continuationPlanEntry = continuationPlanEntryRepository.findUnique(continuationPlan, newDate);
        transactionService.nextTransaction();

        // then
        Assertions.assertThat(continuationPlanEntryRepository.listAll()).hasSize(2);
        Assertions.assertThat(continuationPlanEntry).isNotNull();
        Assertions.assertThat(continuationPlanEntry).isNotEqualTo(newContinuationPlanEntry2);
        Assertions.assertThat(continuationPlanEntry).isEqualTo(newContinuationPlanEntry3);
        Assertions.assertThat(continuationPlanEntry.getContinuationPlan()).isEqualTo(continuationPlan);
        Assertions.assertThat(continuationPlanEntry.getDate()).isEqualTo(newDate);
        Assertions.assertThat(continuationPlanEntry.getPercentage()).isEqualTo(newPercentage);
        Assertions.assertThat(continuationPlanEntry.getEntryValues()).isEmpty();

    }


    @Inject
    TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

    @Inject
    ContinuationPlanRepository continuationPlanRepository;

    @Inject
    ContinuationPlanEntryRepository continuationPlanEntryRepository;

}
