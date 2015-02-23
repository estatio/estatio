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

import java.util.List;

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
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

public class UnitsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new PropertyForOxf());
                executionContext.executeChild(this, new PropertyForKal());
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
            final Unit unit = units.findUnitByReference(PropertyForOxf.unitReference("001"));
            // then
            Assert.assertEquals("OXF-001", unit.getReference());
        }

        @Test
        public void findByReferenceOrName() throws Exception {
            // given
            assertThat(units.findUnits("*XF*", false).size(), is(25));

            // when
            Unit unit = units.findUnitByReference(PropertyForOxf.unitReference("001"));
            unit.setEndDate(new LocalDate(2014, 1, 1));

            // then
            assertThat(units.findUnits("*XF*", false).size(), is(24));
            assertThat(units.findUnits("*XF*", true).size(), is(25));
        }

    }

    public static class FindActiveByProperty extends UnitsTest {

        @Test
        public void findActiveByProperty() throws Exception {
            // given
            Property propertyForOxf = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
            List<Unit> results = units.findActiveByProperty(propertyForOxf);
            assertThat(results.size(), is(25));

            // when
            results.get(0).setEndDate(new LocalDate(2013, 1, 1));

            // then
            assertThat(units.findActiveByProperty(propertyForOxf).size(), is(24));
        }
    }
}