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
package org.estatio.integtests.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.lease.LeaseForKalPoison001;
import org.estatio.fixture.lease.LeaseForOxfMediaX002;
import org.estatio.fixture.lease.LeaseForOxfMiracl005;
import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.fixture.lease.LeaseForOxfPret004;
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

public class LeasesTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    public static class FindExpireInDateRange extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                    executionContext.executeChild(this, new LeaseForOxfMediaX002());
                    executionContext.executeChild(this, new LeaseForOxfPoison003());
                    executionContext.executeChild(this, new LeaseForKalPoison001());
                    executionContext.executeChild(this, new LeaseForOxfPret004());
                    executionContext.executeChild(this, new LeaseForOxfMiracl005());
                }
            });
        }

        @Test
        public void whenLeasesExpiringInRange() {
            // given
            final LocalDate startDate = VT.ld(2020, 1, 1);
            final LocalDate endDate = VT.ld(2030, 1, 1);
            // when
            final List<Lease> matchingLeases = leases.findExpireInDateRange(startDate, endDate);
            // then
            assertThat(matchingLeases.size(), is(4));
        }

    }

    public static class FindLeaseByReference extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                }
            });
        }

        @Test
        public void whenValidReference() {
            final Lease lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
            Assert.assertEquals(LeaseForOxfTopModel001.LEASE_REFERENCE, lease.getReference());
        }

    }

    public static class FindLeases extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                    executionContext.executeChild(this, new LeaseForOxfMediaX002());
                    executionContext.executeChild(this, new LeaseForOxfPoison003());
                    executionContext.executeChild(this, new LeaseForKalPoison001());
                    executionContext.executeChild(this, new LeaseForOxfPret004());
                    executionContext.executeChild(this, new LeaseForOxfMiracl005());
                }
            });
        }

        @Test
        public void whenWildcard() {
            final List<Lease> matchingLeases = leases.findLeases("OXF*");
            assertThat(matchingLeases.size(), is(5));
        }

    }

    public static class FindLeasesByProperty extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                    executionContext.executeChild(this, new LeaseForOxfMediaX002());
                    executionContext.executeChild(this, new LeaseForOxfPoison003());
                    executionContext.executeChild(this, new LeaseForKalPoison001());
                    executionContext.executeChild(this, new LeaseForOxfPret004());
                    executionContext.executeChild(this, new LeaseForOxfMiracl005());
                }
            });
        }

        @Inject
        private Properties properties;

        @Test
        public void whenValidProperty() {
            // given
            final Property property = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
            // when
            final List<Lease> matchingLeases = leases.findLeasesByProperty(property);
            // then
            assertThat(matchingLeases.size(), is(4));
        }

    }

    public static class FindByBrand extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                }
            });
        }

        @Inject
        private Brands brands;

        @Test
        public void whenValidProperty() {
            // given
            final Brand brand = brands.findByName(LeaseForOxfTopModel001.BRAND);
            // when
            final List<Lease> matchingLeases = leases.findByBrand(brand);
            // then
            assertThat(matchingLeases.size(), is(1));
        }

    }

    public static class FindLeasesActiveOnDate extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                }
            });
        }

        @Inject
        private Properties properties;

        @Test
        public void whenValidProperty() {
            // given
            final Property property = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
            System.out.println(property);
            // when
            assertThat(leases.findLeasesActiveOnDate(property, new LocalDate(2010, 7, 14)).size(), is(0));
            assertThat(leases.findLeasesActiveOnDate(property, new LocalDate(2010, 7, 15)).size(), is(1));
        }
    }

    public static class Renew extends LeasesTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001());
                }
            });
        }

        @Inject
        private AgreementRoles agreementRoles;

        @Inject
        private AgreementRoleTypes agreementRoleTypes;

        @Test
        public void renew() {
            Lease lease = leases.allLeases().get(0);
            String newReference = lease.default0Renew() + "-2";
            String newName = lease.default1Renew() + "-2";
            LocalDate newStartDate = lease.default2Renew();
            LocalDate newEndDate = new LocalDate(2030, 12, 31);
            Lease newLease = lease.renew(newReference, newName, newStartDate, newEndDate, true);

            // Old lease
            assertThat(lease.getTenancyEndDate(), is(newStartDate.minusDays(1)));

            //
            assertThat(newLease.getOccupancies().size(), is(1));

            // New lease
            assertThat(newLease.getStartDate(), is(newStartDate));
            assertThat(newLease.getEndDate(), is(newEndDate));
            assertThat(newLease.getTenancyStartDate(), is(newStartDate));
            assertThat(newLease.getTenancyEndDate(), is(newEndDate));

            // Then
            assertThat(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(lease, lease.getSecondaryParty(), agreementRoleTypes.findByTitle("Tenant"), lease.getStartDate()).getCommunicationChannels().size(), is(2));
  
        }

    }

}