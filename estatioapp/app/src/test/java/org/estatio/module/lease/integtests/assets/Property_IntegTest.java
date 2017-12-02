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
package org.estatio.module.lease.integtests.assets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.asset.app.PropertyMenu;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.fixtures.property.builders.PropertyAndUnitsAndOwnerAndManagerBuilder;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.contributions.Property_vacantUnits;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class Property_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Inject
    PropertyMenu propertyMenu;
    @Inject
    PropertyRepository propertyRepository;
    @Inject
    OccupancyRepository occupancyRepository;

    public static class GetUnits extends Property_IntegTest {

        @Test
        public void whenReturnsInstance_thenCanTraverseUnits() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            // when
            Set<Unit> units = property.getUnits();

            // then
            assertThat(units).hasSize(25);
        }

        @Test
        public void occupiedUnits() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            Set<Unit> allUnits = property.getUnits();
            Set<Unit> occupiedUnits = occupancyRepository.findByProperty(property)
                    .stream()
                    .map(Occupancy::getUnit)
                    .collect(Collectors.toSet());

            assertThat(allUnits).hasSize(25);
            assertThat(occupiedUnits).isNotEmpty();

            // When
            List<Unit> vacantUnits = wrap(mixin(Property_vacantUnits.class, property)).$$();

            // Then
            assertThat(vacantUnits).isNotEmpty();
            assertThat(vacantUnits.size()).isEqualTo(allUnits.size() - occupiedUnits.size());
        }
    }

    public static class Dispose extends Property_IntegTest {

        private PropertyAndUnitsAndOwnerAndManagerBuilder fs;

        @Before
        public void setupData() {
            fs = new PropertyAndUnitsAndOwnerAndManagerBuilder();

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, fs);
                }
            });
        }

        @Inject
        PropertyMenu propertyMenu;
        @Inject
        ClockService clockService;
        @Inject
        FakeDataService fakeDataService;

        @Test
        public void happyCase() throws Exception {

            //
            // given
            //
            final Property property = fs.getObject();
            assertThat(property.getDisposalDate()).isNull();

            //
            // when
            //
            final LocalDate disposalDate = clockService.now().plusDays(fakeDataService.ints().between(10, 20));
            wrap(property).dispose(disposalDate);

            //
            // then
            //
            assertThat(property.getDisposalDate()).isEqualTo(disposalDate);
        }

        @Test
        public void whenAlreadyDisposed() throws Exception {

            //
            // given
            //
            final Property property = fs.getObject();

            //
            // and given
            //
            final LocalDate disposalDate = clockService.now().plusDays(fakeDataService.ints().between(10, 20));
            wrap(property).dispose(disposalDate);

            assertThat(property.getDisposalDate()).isEqualTo(disposalDate);

            //
            // expect
            //
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage("already disposed");

            //
            // when
            //
            final LocalDate disposalDate2 = clockService.now().plusDays(fakeDataService.ints().between(30, 40));
            wrap(property).dispose(disposalDate);

            //
            // then
            //
            assertThat(property.getDisposalDate()).isEqualTo(disposalDate);

        }

    }
}