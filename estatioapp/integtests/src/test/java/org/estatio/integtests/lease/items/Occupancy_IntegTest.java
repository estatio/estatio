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

import org.estatio.module.lease.dom.Lease;
import org.estatio.app.menus.lease.LeaseMenu;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.Occupancy;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class Occupancy_IntegTest extends EstatioIntegrationTest {

    public static class GetBrand extends Occupancy_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
        }

        @Inject
        private LeaseMenu leaseMenu;

        @Inject
        private LeaseRepository leaseRepository;

        private Lease leaseTopModel;
        private Occupancy occupancy;

        @Before
        public void setup() {
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            occupancy = leaseTopModel.primaryOccupancy().get();
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