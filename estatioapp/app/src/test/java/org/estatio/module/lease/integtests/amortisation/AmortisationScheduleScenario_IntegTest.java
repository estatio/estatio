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
package org.estatio.module.lease.integtests.amortisation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleService;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule_leaseAmendmentItems;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule_leaseItems;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class AmortisationScheduleScenario_IntegTest extends LeaseModuleIntegTestAbstract {

    AmortisationSchedule schedule;
    LocalDate startDate;
    LocalDate endDate;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb);
            }
        });
    }

    @Test
    public void full_happy_case_scenario_using_service_create_schedule_for_lease_and_charge() throws Exception {

        // given an applied amendment
        Lease topmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(topmodelLease.findItemsOfType(LeaseItemType.RENT)).hasSize(1);
        assertThat(topmodelLease.findItemsOfType(LeaseItemType.RENT_DISCOUNT)).isEmpty();
        final LeaseAmendment topModelAmendment = leaseAmendmentRepository.findByLease(topmodelLease).stream().findFirst()
                .orElse(null);
        wrap(topModelAmendment).sign(topmodelLease.getStartDate());
        wrap(topModelAmendment).apply();
        transactionService.nextTransaction();
        assertThat(topmodelLease.findItemsOfType(LeaseItemType.RENT_DISCOUNT)).hasSize(1);
        final LeaseItem discountItem = topmodelLease.findItemsOfType(LeaseItemType.RENT_DISCOUNT).stream().findFirst()
                .orElse(null);
        // when
        final AmortisationSchedule schedule = amortisationScheduleService
                .createAmortisationScheduleForLeaseAndCharge(
                        topmodelLease,
                        discountItem.getCharge(),
                        Frequency.MONTHLY,
                        topModelAmendment.getDateSigned(),
                        topModelAmendment.getDateSigned().plusYears(1)
                );

        // then
        assertThat(schedule.getLease()).isEqualTo(topmodelLease);
        assertThat(schedule.getCharge()).isEqualTo(discountItem.getCharge());
        assertThat(schedule.getScheduledAmount()).isEqualTo(new BigDecimal("1638.85"));
        assertThat(schedule.getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(schedule.getStartDate()).isEqualTo(topModelAmendment.getDateSigned());
        assertThat(schedule.getEndDate()).isEqualTo(topModelAmendment.getDateSigned().plusYears(1));
        assertThat(schedule.getNote()).isNull();

        final List<LeaseAmendmentItemForDiscount> amendmentItems = mixin(AmortisationSchedule_leaseAmendmentItems.class, schedule).$$();
        assertThat(amendmentItems).hasSize(1);
        assertThat(mixin(AmortisationSchedule_leaseItems.class, schedule).$$()).hasSize(1);

        assertThat(schedule.getScheduledAmount()).isEqualTo(amendmentItems.get(0).getCalculatedDiscountAmount().negate());
        assertThat(schedule.getEntries()).hasSize(13);
        
    }

    @Inject AmortisationScheduleService amortisationScheduleService;

    @Inject LeaseAmendmentRepository leaseAmendmentRepository;

}