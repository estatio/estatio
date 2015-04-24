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
package org.estatio.integtests.guarantee;

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.Guarantees;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

public class GuaranteeTest_TODO extends EstatioIntegrationTest {

    public static class UpdateOrSomething extends GuaranteeTest_TODO {

        private Lease lease;
        private Guarantee guarantee;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
                }
            }.withTracing());

            lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            guarantee = guarantees.findByReference(_LeaseForOxfTopModel001Gb.REF + "-D");
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
        private Leases leases;

        @Inject
        private Guarantees guarantees;

    }
}