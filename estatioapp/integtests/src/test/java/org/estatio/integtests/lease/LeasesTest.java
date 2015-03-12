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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.lease.LeaseForKalPoison001Nl;
import org.estatio.fixture.lease._LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease._LeaseForOxfMiracl005Gb;
import org.estatio.fixture.lease._LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease._LeaseForOxfPret004Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

public class LeasesTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    public static class FindExpireInDateRange extends LeasesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMediaX002Gb());
                    executionContext.executeChild(this, new _LeaseForOxfPoison003Gb());
                    executionContext.executeChild(this, new LeaseForKalPoison001Nl());
                    executionContext.executeChild(this, new _LeaseForOxfPret004Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMiracl005Gb());
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
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                }
            });
        }

        @Test
        public void whenValidReference() {
            final Lease lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            Assert.assertEquals(_LeaseForOxfTopModel001Gb.REF, lease.getReference());
        }

    }

    public static class FindLeaseByReferenceOrNull extends LeasesTest {

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

        @Test
        public void noResults() {
            final Lease lease = leases.findLeaseByReferenceElseNull("FAKEREF");
            assertNull(lease);
        }
    }

    public static class FindLeases extends LeasesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMediaX002Gb());
                    executionContext.executeChild(this, new _LeaseForOxfPoison003Gb());
                    executionContext.executeChild(this, new LeaseForKalPoison001Nl());
                    executionContext.executeChild(this, new _LeaseForOxfPret004Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMiracl005Gb());
                }
            });
        }

        @Test
        public void whenWildcard() {
            // Given
            final List<Lease> matchingLeases = leases.findLeases("OXF*", false);
            assertThat(matchingLeases.size(), is(5));

            // When
            // terminate one lease...
            Lease oxfTop = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            oxfTop.terminate(new LocalDate(2014, 1, 1), true);

            // Then
            assertThat(oxfTop.getTenancyEndDate(), is(new LocalDate(2014, 1, 1)));
            assertThat(leases.findLeases("OXF*", false).size(), is(4));
            assertThat(leases.findLeases("OXF*", true).size(), is(5));
        }

    }

    public static class FindLeasesByProperty extends LeasesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMediaX002Gb());
                    executionContext.executeChild(this, new _LeaseForOxfPoison003Gb());
                    executionContext.executeChild(this, new LeaseForKalPoison001Nl());
                    executionContext.executeChild(this, new _LeaseForOxfPret004Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMiracl005Gb());
                }
            });
        }

        @Inject
        private Properties properties;

        @Test
        public void whenValidProperty() {
            // given
            final Property property = properties.findPropertyByReference(_PropertyForOxfGb.REF);
            // when
            final List<Lease> matchingLeases = leases.findLeasesByProperty(property);
            // then
            assertThat(matchingLeases.size(), is(4));
        }

    }

    public static class FindByBrand extends LeasesTest {

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
        private Brands brands;

        @Test
        public void whenValidProperty() {
            // given
            final Brand brand = brands.findByName(_LeaseForOxfTopModel001Gb.BRAND);
            final List<Lease> matchingLeases = leases.findByBrand(brand, false);
            assertThat(matchingLeases.size(), is(1));

            // when
            Lease oxfTop = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            oxfTop.terminate(new LocalDate(2014, 1, 1), true);
            final List<Lease> matchingLeases2 = leases.findByBrand(brand, false);

            // then
            assertTrue(matchingLeases2.isEmpty());
            final List<Lease> matchingLeases3 = leases.findByBrand(brand, true);
            assertThat(matchingLeases3.size(), is(1));
        }
    }

    public static class FindLeasesActiveOnDate extends LeasesTest {

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
        private Properties properties;

        @Test
        public void whenValidProperty() {
            // given
            final Property property = properties.findPropertyByReference(_PropertyForOxfGb.REF);
            System.out.println(property);
            // when
            assertThat(leases.findLeasesActiveOnDate(property, new LocalDate(2010, 7, 14)).size(), is(0));
            assertThat(leases.findLeasesActiveOnDate(property, new LocalDate(2010, 7, 15)).size(), is(1));
        }
    }

    public static class Renew extends LeasesTest {

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
        private AgreementRoles agreementRoles;

        @Inject
        private AgreementRoleTypes agreementRoleTypes;

        @Test
        public void renew() {
            // Given
            Lease lease = leases.allLeases().get(0);
            String newReference = lease.default0Renew() + "-2";
            String newName = lease.default1Renew() + "-2";
            LocalDate newStartDate = lease.default2Renew();
            LocalDate newEndDate = new LocalDate(2030, 12, 31);

            // When
            Lease newLease = lease.renew(newReference, newName, newStartDate, newEndDate, true);

            // Then
            assertThat(lease.getTenancyEndDate(), is(newStartDate.minusDays(1)));

            assertThat(newLease.getOccupancies().size(), is(1));
            assertThat(newLease.getStartDate(), is(newStartDate));
            assertThat(newLease.getEndDate(), is(newEndDate));
            assertThat(newLease.getTenancyStartDate(), is(newStartDate));
            assertThat(newLease.getTenancyEndDate(), is(newEndDate));

            // Then
            assertThat(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(newLease, newLease.getSecondaryParty(), agreementRoleTypes.findByTitle("Tenant"), newLease.getStartDate()).getCommunicationChannels().size(), is(2));
            assertThat(newLease.getOccupancies().size(), is(1));

        }

        
        @Test
        public void reneWithTerminatedOccupancies() {
            // Given
            Lease lease = leases.allLeases().get(0);
            String newReference = lease.default0Renew() + "-2";
            String newName = lease.default1Renew() + "-2";
            LocalDate newStartDate = lease.default2Renew();
            LocalDate newEndDate = new LocalDate(2030, 12, 31);

            // When
            lease.getOccupancies().first().setEndDate(lease.getTenancyEndDate());
            Lease newLease = lease.renew(newReference, newName, newStartDate, newEndDate, true);

            // Then
            assertThat(newLease.getOccupancies().size(), is(1));

        }

    }

}