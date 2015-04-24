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
package org.estatio.integtests.lease.items;

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class OccupancyTest extends EstatioIntegrationTest {

    public static class GetBrand extends OccupancyTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                }
            });
        }

        @Inject
        private Leases leases;

        private Lease leaseTopModel;
        private Occupancy occupancy;

        @Before
        public void setup() {
            leaseTopModel = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            occupancy = leaseTopModel.getOccupancies().first();
        }

        @Test
        public void whenNotNull() throws Exception {

            // TODO: this seems to be merely asserting on the contents of the fixture
            final Brand brand = occupancy.getBrand();
            assertThat(brand, is(not(nullValue())));
            assertThat(brand.getName(), is("Topmodel"));
        }

    }
}