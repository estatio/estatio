package org.estatio.integtests.budgetassignment;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgetassignment.ServiceChargeItem;
import org.estatio.dom.budgetassignment.ServiceChargeItemRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceChargeItemRepositoryTest extends EstatioIntegrationTest {

    @Inject
    ServiceChargeItemRepository serviceChargeItemRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    UnitRepository unitRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            }
        });
    }

    public static class FindOrCreate extends ServiceChargeItemRepositoryTest {

        ServiceChargeItem serviceChargeItem;

        @Test
        public void findOrCreateWorksAndIsIdempotent() throws Exception {
            // given
            Unit unit1 = unitRepository.findUnitByReference("OXF-001");
            Occupancy occupancyTopModel = occupancyRepository.findByUnit(unit1).get(0);
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);

            // when
            serviceChargeItem = serviceChargeItemRepository.findOrCreateServiceChargeItem(occupancyTopModel, charge);

            // then
            assertThat(serviceChargeItemRepository.allServiceChargeItems().size()).isEqualTo(1);
            assertThat(serviceChargeItem.getOccupancy()).isEqualTo(occupancyTopModel);
            assertThat(serviceChargeItem.getCharge()).isEqualTo(charge);

            // and when again
            serviceChargeItemRepository.findOrCreateServiceChargeItem(occupancyTopModel, charge);

            // then still
            assertThat(serviceChargeItemRepository.allServiceChargeItems().size()).isEqualTo(1);

        }

    }



}
