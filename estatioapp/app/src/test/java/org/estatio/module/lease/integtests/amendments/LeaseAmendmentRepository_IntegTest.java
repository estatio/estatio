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
package org.estatio.module.lease.integtests.amendments;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentAgreementTypeEnum;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentState;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentTemplate;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.builder());
            }
        });
    }

    @Test
    public void upsert_and_finders_work() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry);
        final LocalDate startDate = new LocalDate(2020, 1, 15);
        final LocalDate endDate = new LocalDate(2020, 3, 31);
        final LeaseAmendmentState state = LeaseAmendmentState.PROPOSED;

        // when
        LeaseAmendment leaseAmendment = leaseAmendmentRepository
                .upsert(lease, LeaseAmendmentTemplate.DEMO_TYPE, state, startDate, endDate);

        // then
        assertThat(leaseAmendment.getLease()).isEqualTo(lease);
        assertThat(leaseAmendment.getState()).isEqualTo(state);
        assertThat(leaseAmendment.getStartDate()).isEqualTo(startDate);
        assertThat(leaseAmendment.getEndDate()).isEqualTo(endDate);
        assertThat(leaseAmendment.getType().getTitle()).isEqualTo(LeaseAmendmentAgreementTypeEnum.LEASE_AMENDMENT.getTitle());
        assertThat(leaseAmendment.getReference()).isEqualTo(lease.getReference().concat(LeaseAmendmentTemplate.DEMO_TYPE.getRef_suffix()));
        assertThat(leaseAmendment.getName()).isEqualTo(lease.getReference().concat(LeaseAmendmentTemplate.DEMO_TYPE.getRef_suffix()));
        assertThat(leaseAmendment.getAtPath()).isEqualTo(lease.getApplicationTenancyPath());
        assertThat(leaseAmendment.getApplicationTenancy()).isEqualTo(lease.getApplicationTenancy());
        assertThat(leaseAmendment.getRoles()).hasSize(2);
        assertThat(leaseAmendment.getPrimaryParty()).isEqualTo(lease.getPrimaryParty());
        assertThat(leaseAmendment.getSecondaryParty()).isEqualTo(lease.getSecondaryParty());

        // and when
        final List<LeaseAmendment> resultForLease = leaseAmendmentRepository.findByLease(lease);
        // then
        assertThat(resultForLease).hasSize(1);
        assertThat(resultForLease.get(0)).isEqualTo(leaseAmendment);

        // and when
        final List<LeaseAmendment> resultForState = leaseAmendmentRepository.findByState(state);
        // then
        assertThat(resultForState).hasSize(1);
        assertThat(resultForState.get(0)).isEqualTo(leaseAmendment);

        // and when
        final List<LeaseAmendment> resultsForDemoType = leaseAmendmentRepository.findByType(LeaseAmendmentTemplate.DEMO_TYPE);
        final List<LeaseAmendment> resultsForDemoTypeOxf = leaseAmendmentRepository.findByTypeAndProperty(
                LeaseAmendmentTemplate.DEMO_TYPE, Property_enum.OxfGb.findUsing(serviceRegistry));
        final List<LeaseAmendment> resultsForDemoTypeRon = leaseAmendmentRepository.findByTypeAndProperty(
                LeaseAmendmentTemplate.DEMO_TYPE, Property_enum.RonIt.findUsing(serviceRegistry));
        final List<LeaseAmendment> resultsForOxf = leaseAmendmentRepository.findByProperty(Property_enum.OxfGb.findUsing(serviceRegistry));
        final List<LeaseAmendment> resultsForRon = leaseAmendmentRepository.findByProperty(Property_enum.RonIt.findUsing(serviceRegistry));
        final List<LeaseAmendment> resultsForOxfProposed = leaseAmendmentRepository.findByPropertyAndState(Property_enum.OxfGb.findUsing(serviceRegistry), LeaseAmendmentState.PROPOSED);
        final List<LeaseAmendment> resultsForOxfSigned = leaseAmendmentRepository.findByPropertyAndState(Property_enum.OxfGb.findUsing(serviceRegistry), LeaseAmendmentState.SIGNED);
        final List<LeaseAmendment> resultsForDemoTypeAndProposed = leaseAmendmentRepository.findByTypeAndState(
                LeaseAmendmentTemplate.DEMO_TYPE, LeaseAmendmentState.PROPOSED);
        final List<LeaseAmendment> resultsForDemoTypeAndSigned = leaseAmendmentRepository.findByTypeAndState(
                LeaseAmendmentTemplate.DEMO_TYPE, LeaseAmendmentState.SIGNED);
        final List<LeaseAmendment> resultsForDemoTypeAndProposedForOxf = leaseAmendmentRepository.findByTypeAndStateAndProperty(
                LeaseAmendmentTemplate.DEMO_TYPE, LeaseAmendmentState.PROPOSED,
                Property_enum.OxfGb.findUsing(serviceRegistry));
        final List<LeaseAmendment> resultsForDemoTypeAndProposedForRon = leaseAmendmentRepository.findByTypeAndStateAndProperty(
                LeaseAmendmentTemplate.DEMO_TYPE, LeaseAmendmentState.PROPOSED,
                Property_enum.RonIt.findUsing(serviceRegistry));
        final List<LeaseAmendment> resultsForOtherType = leaseAmendmentRepository.findByType(LeaseAmendmentTemplate.COVID_ITA_FREQ_CHANGE_ONLY);
        // then
        assertThat(resultsForDemoType).hasSize(1);
        assertThat(resultsForDemoTypeOxf).hasSize(1);
        assertThat(resultsForDemoTypeRon).isEmpty();
        assertThat(resultsForOxf).hasSize(1);
        assertThat(resultsForRon).isEmpty();
        assertThat(resultsForOxfProposed).hasSize(1);
        assertThat(resultsForOxfSigned).isEmpty();
        assertThat(resultsForDemoTypeAndProposed).hasSize(1);
        assertThat(resultsForDemoTypeAndProposedForOxf).hasSize(1);
        assertThat(resultsForDemoType.get(0)).isEqualTo(leaseAmendment);
        assertThat(resultsForDemoTypeAndSigned).isEmpty();
        assertThat(resultsForDemoTypeAndProposedForRon).isEmpty();
        assertThat(resultsForOtherType).isEmpty();

        // and when
        final LeaseAmendmentState adaptedState = LeaseAmendmentState.SIGNED;
        final LocalDate adaptedStartDate = new LocalDate(2020, 1, 16);
        final LocalDate adaptedEndDate = new LocalDate(2020, 4, 1);
        leaseAmendment = leaseAmendmentRepository.upsert(lease, LeaseAmendmentTemplate.DEMO_TYPE,
                adaptedState, adaptedStartDate, adaptedEndDate);
        // then
        assertThat(leaseAmendment.getState()).isEqualTo(adaptedState);
        assertThat(leaseAmendment.getStartDate()).isEqualTo(adaptedStartDate);
        assertThat(leaseAmendment.getEndDate()).isEqualTo(adaptedEndDate);

    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;
}