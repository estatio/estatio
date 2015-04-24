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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractInteractionEvent.Phase;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brand.RemoveEvent;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

public class OccupanciesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
        lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
        unit = units.findUnitByReference(_PropertyForOxfGb.unitReference("001"));
    }

    @Inject
    Leases leases;

    @Inject
    Units units;

    @Inject
    Occupancies occupancies;

    @Inject
    Brands brands;

    Lease lease;

    Unit unit;

    public static class FindByLease extends OccupanciesTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(occupancies.occupancies(lease).size(), is(1));
        }

    }

    public static class FindByUnit extends OccupanciesTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(occupancies.occupancies(unit).size(), is(1));
        }

    }

    public static class FindByLeaseAndUnitAndStartDate extends OccupanciesTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(occupancies.findByLeaseAndUnitAndStartDate(lease, unit, new LocalDate(2010, 7, 15)));
        }

    }

    public static class FindByBrand extends OccupanciesTest {

        @Test
        public void happyCase() throws Exception {
            Brand brand = brands.findByName(_LeaseForOxfTopModel001Gb.BRAND);
            assertNotNull(brand);

            assertThat(occupancies.findByBrand(brand, false).size(), is(1));
            assertThat(occupancies.findByBrand(brand, true).size(), is(1));
        }

    }

    public static class FindByLeaseAndDate extends OccupanciesTest {

        @Test
        public void happyCase() throws Exception {
            List<Occupancy> results = occupancies.findByLeaseAndDate(lease, lease.getStartDate());
            assertThat(results.size(), is(1));
        }
    }

    public static class OnBrandRemoveEvent extends OccupanciesTest {

        Brand oldBrand;
        Brand newBrand;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new _LeaseForOxfMediaX002Gb());
                }
            });

        }

        @Before
        public void setUp() throws Exception {
            oldBrand = brands.findByName(_LeaseForOxfTopModel001Gb.BRAND);
            newBrand = brands.findByName(_LeaseForOxfMediaX002Gb.BRAND);
        }

        @Test
        public void invalidBecauseNoReplacement() throws Exception {
            // when
            Brand.RemoveEvent event = new RemoveEvent(oldBrand, null, (Object[]) null);
            event.setPhase(Phase.VALIDATE);
            occupancies.on(event);

            // then
            assertTrue(event.isInvalid());
        }

        @Test
        public void executingReplacesBrand() throws Exception {
            // when
            Brand.RemoveEvent event = new RemoveEvent(oldBrand, null, newBrand);
            event.setPhase(Phase.VALIDATE);
            occupancies.on(event);
            event.setPhase(Phase.EXECUTING);
            occupancies.on(event);

            /*
             * then Topmodel brand should be adopted by the MEDIAX brand. So,
             * there should be 2
             * MEDIAX occupancies and 0 TOPMODEL occupancies.
             */
            assertThat(occupancies.findByBrand(oldBrand, false).size(), is(0));
            assertThat(occupancies.findByBrand(newBrand, false).size(), is(2));
        }

        @Test
        public void whenVetoingSubscriber() {
            // then
            expectedException.expect(InvalidException.class);

            // when
            wrap(oldBrand).remove();
        }

    }

}