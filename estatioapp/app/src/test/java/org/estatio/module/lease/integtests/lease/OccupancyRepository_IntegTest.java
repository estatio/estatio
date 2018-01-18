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

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.Brand.RemoveEvent;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceChargeBudgeted_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OccupancyRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                executionContext.executeChild(this, LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

            }
        });
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        unit = unitRepository.findUnitByReference(Property_enum.OxfGb.unitRef("001"));
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    BrandRepository brandRepository;

    Lease lease;

    Unit unit;

    public static class FindByLease extends OccupancyRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(occupancyRepository.findByLease(lease).size(), is(1));
        }

    }

    public static class FindByUnit extends OccupancyRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(occupancyRepository.findByUnit(unit).size(), is(1));
        }

    }

    public static class FindByLeaseAndUnitAndStartDate extends OccupancyRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(occupancyRepository.findByLeaseAndUnitAndStartDate(lease, unit, new LocalDate(2010, 7, 15)));
        }

    }

    public static class FindByBrand extends OccupancyRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            Brand brand = brandRepository.findByName(Lease_enum.OxfTopModel001Gb.getOccupancySpecs()[0].getBrand());
            assertNotNull(brand);

            assertThat(occupancyRepository.findByBrand(brand, false).size(), is(1));
            assertThat(occupancyRepository.findByBrand(brand, true).size(), is(1));
        }

    }

    public static class FindByLeaseAndDate extends OccupancyRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            List<Occupancy> results = occupancyRepository.findByLeaseAndDate(lease, lease.getStartDate());
            assertThat(results.size(), is(1));
        }
    }

    public static class OnBrandRemoveEvent extends OccupancyRepository_IntegTest {

        Brand oldBrand;
        Brand newBrand;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.builder());
                }
            });
        }

        @Before
        public void setUp() throws Exception {
            oldBrand = brandRepository.findByName(Lease_enum.OxfTopModel001Gb.getOccupancySpecs()[0].getBrand());
            newBrand = brandRepository.findByName(Lease_enum.OxfMediaX002Gb.getOccupancySpecs()[0].getBrand());
        }

        @Test
        public void invalidBecauseNoReplacement() throws Exception {
            // when
            Brand.RemoveEvent event = new RemoveEvent();
            event.setSource(oldBrand);
            event.setArguments(Lists.newArrayList());
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            occupancyRepository.on(event);

            // then
            assertTrue(event.isInvalid());
        }

        @Test
        public void executingReplacesBrand() throws Exception {
            // when
            Brand.RemoveEvent event = new RemoveEvent();
            event.setSource(oldBrand);
            event.setArguments(Lists.newArrayList(newBrand));
            event.setEventPhase(AbstractDomainEvent.Phase.VALIDATE);
            occupancyRepository.on(event);
            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTING);
            occupancyRepository.on(event);

            /*
             * then Topmodel brand should be adopted by the MEDIAX brand. So,
             * there should be 2
             * MEDIAX occupancies and 0 TOPMODEL occupancies.
             */
            assertThat(occupancyRepository.findByBrand(oldBrand, false).size(), is(0));
            assertThat(occupancyRepository.findByBrand(newBrand, false).size(), is(2));
        }

        @Test
        public void whenVetoingSubscriber() {
            // then
            expectedException.expect(InvalidException.class);

            // when
            wrap(oldBrand).remove();
        }

    }

    public static class OnChangeDateEvent extends OccupancyRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        private Lease leaseTopModel;
        private Occupancy occupancy;

        @Before
        public void setup() {
            leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            occupancy = leaseTopModel.primaryOccupancy().get();
        }

        @Test
        public void onDateChange() throws Exception {
            // Given that occupancy dates are equal to lease dates
            assertEquals(occupancy.getStartDate(), leaseTopModel.getTenancyStartDate());

            // When lease tenancy start date changes
            LocalDate newTenancyStartDate = new LocalDate(2015, 12, 31);
            wrap(leaseTopModel).changeTenancyDates(newTenancyStartDate, leaseTopModel.getTenancyEndDate());

            // Then assert that occupancy date has changed too
            assertThat(occupancy.getStartDate(), is(newTenancyStartDate));
            assertEquals(occupancy.getStartDate(), leaseTopModel.getTenancyStartDate());
        }
    }

    public static class OnTerminateEvent extends OccupancyRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        private Lease leaseTopModel;
        private Occupancy occupancy;

        @Before
        public void setup() {
            leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            occupancy = leaseTopModel.primaryOccupancy().get();
        }

        @Test
        public void onTerminate() throws Exception {
            // Given that occupancy has no end date
            assertNull(occupancy.getEndDate());

            // When lease is terminated
            LocalDate terminationDate = new LocalDate(2015, 12, 31);
            wrap(leaseTopModel).terminate(terminationDate);

            // Then assert that occupancy end date is set too
            assertThat(occupancy.getEndDate(), is(terminationDate));
            assertEquals(occupancy.getEndDate(), leaseTopModel.getTenancyEndDate());
        }
    }
}