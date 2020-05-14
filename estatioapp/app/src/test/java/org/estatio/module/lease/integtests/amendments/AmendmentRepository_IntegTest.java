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

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.amendments.Amendment;
import org.estatio.module.lease.dom.amendments.AmendmentAgreementTypeEnum;
import org.estatio.module.lease.dom.amendments.AmendmentRepository;
import org.estatio.module.lease.dom.amendments.AmendmentState;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class AmendmentRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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
    public void xxx() throws Exception {

        // given
        final Lease lease = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry);
        final LocalDate startDate = new LocalDate(2020, 1, 15);
        final LocalDate endDate = new LocalDate(2020, 3, 31);
        final AmendmentState state = AmendmentState.PROPOSED;

        // when
        final Amendment amendment = amendmentRepository.create(lease, state, startDate, endDate);

        // then
        assertThat(amendment.getLease()).isEqualTo(lease);
        assertThat(amendment.getState()).isEqualTo(state);
        assertThat(amendment.getStartDate()).isEqualTo(startDate);
        assertThat(amendment.getEndDate()).isEqualTo(endDate);
        assertThat(amendment.getType().getTitle()).isEqualTo(AmendmentAgreementTypeEnum.AMENDMENT.getTitle());
        assertThat(amendment.getReference()).isEqualTo(lease.getReference());
        assertThat(amendment.getName()).isEqualTo(lease.getReference().concat(AmendmentRepository.NAME_SUFFIX));
        assertThat(amendment.getAtPath()).isEqualTo(lease.getApplicationTenancyPath());
        assertThat(amendment.getApplicationTenancy()).isEqualTo(lease.getApplicationTenancy());
        assertThat(amendment.getRoles()).hasSize(2);
        assertThat(amendment.getPrimaryParty()).isEqualTo(lease.getPrimaryParty());
        assertThat(amendment.getSecondaryParty()).isEqualTo(lease.getSecondaryParty());

        // and when
        final List<Amendment> resultForLease = amendmentRepository.findByLease(lease);
        // then
        assertThat(resultForLease).hasSize(1);
        assertThat(resultForLease.get(0)).isEqualTo(amendment);

        // and when
        final List<Amendment> resultForState = amendmentRepository.findByState(state);
        // then
        assertThat(resultForState).hasSize(1);
        assertThat(resultForState.get(0)).isEqualTo(amendment);

    }

    @Inject
    AmendmentRepository amendmentRepository;
}