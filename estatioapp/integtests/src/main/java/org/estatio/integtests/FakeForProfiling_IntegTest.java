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
package org.estatio.integtests;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.application.fixtures.EstatioOperationalTeardownFixture;
import org.estatio.module.application.fixtures.EstatioRefDataSetupFixture;
import org.estatio.module.base.fixtures.security.EstatioSecurityModuleDemoFixture;

public class FakeForProfiling_IntegTest extends EstatioIntegrationTest {



    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioOperationalTeardownFixture());
                executionContext.executeChild(this, new EstatioRefDataSetupFixture());
                executionContext.executeChild(this, new EstatioSecurityModuleDemoFixture());
            }
        });

    }

    @Test
    public void dummy() throws Exception {
        // DO NOTHING
    }



}