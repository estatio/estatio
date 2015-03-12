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

import java.util.Set;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyBuilder;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertyTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
            runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new _PropertyForOxfGb());
            }
        });
    }

    @Inject
    Properties properties;


    public static class GetUnits extends PropertyTest {

        @Test
        public void whenReturnsInstance_thenCanTraverseUnits() throws Exception {
            // given
            Property property = properties.findPropertyByReference(_PropertyForOxfGb.REF);

            // when
            Set<Unit> units = property.getUnits();

            // then
            assertThat(units.size(), is(25));
        }
    }

    public static class Dispose extends PropertyTest {

        private PropertyBuilder fs;

        @Before
        public void setupData() {
            fs = new PropertyBuilder();

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, fs);
                }
            });
        }

        @Inject
        private Properties properties;
        @Inject
        private ClockService clockService;

        @Test
        public void happyCase() throws Exception {

            //
            // given
            //
            final Property property = fs.getProperty();
            Assertions.assertThat(property.getDisposalDate()).isNull();

            //
            // when
            //
            final LocalDate disposalDate = clockService.now().plusDays(fs.faker().values().anInt(10,20));
            wrap(property).dispose(disposalDate, true);

            //
            // then
            //
            Assertions.assertThat(property.getDisposalDate()).isEqualTo(disposalDate);
        }

        @Test
        public void whenDontConfirm() throws Exception {

            //
            // given
            //
            final Property property = fs.getProperty();
            Assertions.assertThat(property.getDisposalDate()).isNull();

            //
            // when
            //
            final LocalDate disposalDate = clockService.now().plusDays(fs.faker().values().anInt(10,20));
            wrap(property).dispose(disposalDate, false);

            //
            // then
            //
            Assertions.assertThat(property.getDisposalDate()).isNull();
        }

        @Test
        public void whenAlreadyDisposed() throws Exception {

            //
            // given
            //
            final Property property = fs.getProperty();

            //
            // and given
            //
            final LocalDate disposalDate = clockService.now().plusDays(fs.faker().values().anInt(10,20));
            wrap(property).dispose(disposalDate, true);

            Assertions.assertThat(property.getDisposalDate()).isEqualTo(disposalDate);


            //
            // expect
            //
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage(containsString("already disposed"));


            //
            // when
            //
            final LocalDate disposalDate2 = clockService.now().plusDays(fs.faker().values().anInt(30,40));
            wrap(property).dispose(disposalDate, true);


            //
            // then
            //
            Assertions.assertThat(property.getDisposalDate()).isEqualTo(disposalDate);

        }

    }
}