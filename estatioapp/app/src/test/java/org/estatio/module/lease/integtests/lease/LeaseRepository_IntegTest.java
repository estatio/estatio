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
package org.estatio.module.lease.integtests.lease;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.base.integtests.VT;

import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.asset.app.PropertyMenu;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class LeaseRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    LeaseRepository leaseRepository;

    public static class FindExpireInDateRange extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.KalPoison001Nl.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfPret004Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb.toBuilderScript());
                }
            });
        }

        @Test
        public void whenLeasesExpiringInRange() {
            // given
            final LocalDate startDate = VT.ld(2020, 1, 1);
            final LocalDate endDate = VT.ld(2030, 1, 1);
            // when
            final List<Lease> matchingLeases = leaseRepository.findExpireInDateRange(startDate, endDate);
            // then
            assertThat(matchingLeases.size()).isEqualTo(4);
        }

    }

    public static class FindLeaseByReference extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                }
            });
        }

        @Test
        public void whenValidReference() {
            // when
            final Lease lease = leaseRepository.findLeaseByReference(Lease_enum.OxfTopModel001Gb.getRef());

            // then
            assertThat(Lease_enum.OxfTopModel001Gb.getRef()).isEqualTo(lease.getReference());
        }

    }

    public static class FindLeaseByReferenceOrNull extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                }
            });
        }

        @Test
        public void noResults() {
            final Lease lease = leaseRepository.findLeaseByReferenceElseNull("FAKEREF");
            assertThat(lease).isNull();
        }
    }

    public static class FindLeases extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.KalPoison001Nl.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfPret004Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb.toBuilderScript());
                }
            });
        }

        @Test
        public void match_external_reference() throws Exception {
            //Given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            lease.setExternalReference("ExTrEf");

            // When Then
            assertThat(leaseRepository.matchByReferenceOrName("*ref", true)).contains(lease);
        }

        @Test
        public void whenWildcard() {
            // Given
            final List<Lease> allLeasesBefore = leaseRepository.matchByReferenceOrName("OXF*", true);
            final List<Lease> unterminatedLeasesBefore = leaseRepository.matchByReferenceOrName("OXF*", false);
            assertThat(unterminatedLeasesBefore.size()).isGreaterThan(0);

            // When terminate one lease...
            final LocalDate yesterday = clockService.now().plusDays(-1);
            final Lease leaseBeingTerminated = fakeDataService.collections().anyOf(unterminatedLeasesBefore);

            wrap(leaseBeingTerminated).terminate(yesterday);

            // Then
            assertThat(leaseBeingTerminated.getTenancyEndDate()).isEqualTo(yesterday);

            final List<Lease> allLeasesAfter =
                    leaseRepository.matchByReferenceOrName("OXF*", true);
            assertThat(allLeasesAfter.size()).isEqualTo(allLeasesBefore.size());

            final List<Lease> unterminatedLeases =
                    leaseRepository.matchByReferenceOrName("OXF*", false);
            assertThat(unterminatedLeases.size()).isEqualTo(unterminatedLeasesBefore.size() - 1);
        }

        @Inject
        ClockService clockService;


    }

    public static class FindLeasesByProperty extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.KalPoison001Nl.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfPret004Gb.toBuilderScript());
                    executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb.toBuilderScript());
                }
            });
        }

        @Inject
        private PropertyMenu propertyMenu;
        @Inject
        PropertyRepository propertyRepository;

        @Test
        public void whenValidProperty() {
            // given
            final Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            // when
            final List<Lease> matchingLeases = leaseRepository.findLeasesByProperty(property);
            // then
            assertThat(matchingLeases.size()).isEqualTo(4);
        }

    }

    public static class FindByBrand extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                }
            });
        }

        @Inject
        private BrandRepository brandRepository;

        @Test
        public void whenValidProperty() {
            // given
            final Brand brand = brandRepository.findByName(Lease_enum.OxfTopModel001Gb.getOccupancySpecs()[0].getBrand());
            final List<Lease> matchingLeases = leaseRepository.findByBrand(brand, false);
            assertThat(matchingLeases.size()).isEqualTo(1);

            // when
            Lease oxfTop = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            oxfTop.terminate(new LocalDate(2014, 1, 1));
            final List<Lease> matchingLeases2 = leaseRepository.findByBrand(brand, false);

            // then
            assertTrue(matchingLeases2.isEmpty());
            final List<Lease> matchingLeases3 = leaseRepository.findByBrand(brand, true);
            assertThat(matchingLeases3.size()).isEqualTo(1);
        }
    }

    public static class FindLeasesActiveOnDate extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                }
            });
        }

        @Inject
        PropertyRepository propertyRepository;

        @Test
        public void whenValidProperty() {
            // given
            final Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            System.out.println(property);
            // when
            assertThat(leaseRepository.findByAssetAndActiveOnDate(property, new LocalDate(2010, 7, 14)).size()).isEqualTo(0);
            assertThat(leaseRepository.findByAssetAndActiveOnDate(property, new LocalDate(2010, 7, 15)).size()).isEqualTo(1);
        }
    }

    public static class Renew extends LeaseRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
                }
            });
        }

        @Inject
        private AgreementRoleRepository agreementRoles;

        @Inject
        private AgreementRoleTypeRepository agreementRoleTypeRepository;

        @Test
        public void renew() {
            // Given
            Lease lease = leaseRepository.allLeases().get(0);
            String newReference = lease.default0Renew() + "-2";
            String newName = lease.default1Renew() + "-2";
            LocalDate newStartDate = lease.default2Renew().plusDays(5); // +5 is to ensure that the change in tenancy end date is detected by the test
            LocalDate newEndDate = new LocalDate(2030, 12, 31);
            lease.setComments("Some comments");

            // When
            Lease newLease = lease.renew(newReference, newName, newStartDate, newEndDate);

            // Then

            // the lease is terminated
            assertThat(lease.getTenancyEndDate()).isEqualTo(newStartDate.minusDays(1));
            assertThat(lease.getOccupancies().first().getEndDate()).isEqualTo(newStartDate.minusDays(1));

            assertThat(newLease.getOccupancies().size()).isEqualTo(1);
            assertThat(newLease.getStartDate()).isEqualTo(newStartDate);
            assertThat(newLease.getEndDate()).isEqualTo(newEndDate);
            assertThat(newLease.getTenancyStartDate()).isEqualTo(newStartDate);
            assertThat(newLease.getTenancyEndDate()).isEqualTo(newEndDate);
            assertThat(newLease.getComments()).isEqualTo("Some comments");

            // Then
            assertThat(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(newLease, newLease.getSecondaryParty(), agreementRoleTypeRepository
                    .findByTitle("Tenant"), newLease.getStartDate()).getCommunicationChannels().size()).isEqualTo(2);
            assertThat(newLease.getOccupancies().size()).isEqualTo(1);
        }

        @Test
        public void reneWithTerminatedOccupancies() {
            // Given
            Lease lease = leaseRepository.allLeases().get(0);
            String newReference = lease.default0Renew() + "-2";
            String newName = lease.default1Renew() + "-2";
            LocalDate newStartDate = lease.default2Renew();
            LocalDate newEndDate = new LocalDate(2030, 12, 31);

            // When
            lease.primaryOccupancy().get().setEndDate(lease.getTenancyEndDate());
            Lease newLease = lease.renew(newReference, newName, newStartDate, newEndDate);

            // Then
            assertThat(newLease.getOccupancies().size()).isEqualTo(1);
        }

    }

    @Inject
    FakeDataService fakeDataService;

}