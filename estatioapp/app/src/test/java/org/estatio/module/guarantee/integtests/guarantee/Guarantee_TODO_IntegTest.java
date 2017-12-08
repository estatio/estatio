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
package org.estatio.module.guarantee.integtests.guarantee;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.fixtures.personas.GuaranteeForOxfTopModel001Gb;
import org.estatio.module.guarantee.integtests.GuaranteeModuleIntegTestAbstract;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

public class Guarantee_TODO_IntegTest extends GuaranteeModuleIntegTestAbstract {

    public static class UpdateOrSomething extends Guarantee_TODO_IntegTest {

        private Lease lease;
        private Guarantee guarantee;

        @Before
        public void setupData() {
            runFixtureScript(new GuaranteeForOxfTopModel001Gb());

            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            guarantee = guaranteeRepository.findByReference(Lease_enum.OxfTopModel001Gb.getRef() + "-D");
        }

        @Test
        public void happyCase() throws Exception {

            // when

            // then
        }

        @Ignore("TODO")
        @Test
        public void sadCase1() throws Exception {

            // when

            // then
        }

        @Ignore("TODO")
        @Test
        public void sadCase2() throws Exception {

            // when

            // then
        }

        @Inject
        private LeaseMenu leaseMenu;

        @Inject
        private LeaseRepository leaseRepository;

        @Inject
        private GuaranteeRepository guaranteeRepository;

    }
}