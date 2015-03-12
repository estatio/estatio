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
package org.estatio.integtests.assets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class UnitsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new _PropertyForOxfGb());
                executionContext.executeChild(this, new PropertyForKalNl());
            }
        });
    }

    @Inject
    Units units;

    @Inject
    Properties properties;

    public static class FindUnitByReference extends UnitsTest {

        @Test
        public void findByReference() throws Exception {
            final Unit unit = units.findUnitByReference(_PropertyForOxfGb.unitReference("001"));
            // then
            Assert.assertEquals("OXF-001", unit.getReference());
        }

        @Test
        public void findByReferenceOrName() throws Exception {
            // given
            assertThat(units.findUnits("*XF*", false).size(), is(25));

            // when
            Unit unit = units.findUnitByReference(_PropertyForOxfGb.unitReference("001"));
            unit.setEndDate(new LocalDate(2014, 1, 1));

            // then
            assertThat(units.findUnits("*XF*", false).size(), is(24));
            assertThat(units.findUnits("*XF*", true).size(), is(25));
        }

    }

    public static class FindByPropertyAndActiveOnDate extends UnitsTest {

        @Test
        public void findActiveByProperty() throws Exception {
            // given
            Property propertyForOxf = properties.findPropertyByReference(_PropertyForOxfGb.REF);

            // when
            Unit unit = units.findUnitByReference(_PropertyForOxfGb.REF + "-001");
            LocalDate startDate = new LocalDate(2013, 1, 1);
            LocalDate endDate = new LocalDate(2013, 12, 31);
            unit.setEndDate(endDate);
            unit.setStartDate(startDate);

            // then
            assertThat(units.findByPropertyAndActiveOnDate(propertyForOxf, startDate).size(), is(25));
            assertThat(units.findByPropertyAndActiveOnDate(propertyForOxf, startDate.minusDays(1)).size(), is(24));
            assertThat(units.findByPropertyAndActiveOnDate(propertyForOxf, endDate).size(), is(25));
            assertThat(units.findByPropertyAndActiveOnDate(propertyForOxf, endDate.plusDays(1)).size(), is(24));
        }
    }

    public static class FindByProperty extends UnitsTest {

        @Test
        public void findActiveByProperty() throws Exception {
            // given, when
            Property propertyForOxf = properties.findPropertyByReference(_PropertyForOxfGb.REF);
            // then
            assertThat(units.findByProperty(propertyForOxf).size(), is(25));
        }
    }
}
