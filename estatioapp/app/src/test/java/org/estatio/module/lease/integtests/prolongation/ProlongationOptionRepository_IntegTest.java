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
package org.estatio.module.lease.integtests.prolongation;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOption;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOptionRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.prolongation.personas.LeaseProlongationOptionsForOxfTopModel001;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class ProlongationOptionRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    ProlongationOptionRepository prolongationOptionRepository;

    @Inject
    LeaseRepository leaseRepository;

    public static class FindByLease extends ProlongationOptionRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new LeaseProlongationOptionsForOxfTopModel001());
                }
            });
        }

        @Test
        public void findByLease() throws Exception {
            // given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

            // when
            final ProlongationOption option = prolongationOptionRepository.findByLease(lease);

            // then
            assertThat(option).isNotNull();
            assertThat(option.getProlongationPeriod()).isEqualToIgnoringCase("5y");
            assertThat(option.getNotificationPeriod()).isEqualToIgnoringCase("6m");
        }
    }

}