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

import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnitsTest extends EstatioIntegrationTest {

    public static class FindUnitByReference extends UnitsTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executeChild(new EstatioBaseLineFixture(), executionContext);

                    executeChild(new PropertyForOxf(), executionContext);
                    executeChild(new PropertyForKal(), executionContext);
                }
            });
        }

        @Inject
        private Units units;

        @Test
        public void findByReference() throws Exception {
            final Unit unit = units.findUnitByReference(PropertyForOxf.unitReference("001"));
            // then
            Assert.assertEquals("OXF-001", unit.getReference());
        }

        @Test
        public void findByReferenceOrName() throws Exception {
            assertThat(units.findUnits("*XF*").size(), is(25));
        }

    }
}