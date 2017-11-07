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
package org.estatio.module.numerator.integtests;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import org.apache.isis.applib.AppManifestAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.security.SecurityModule;

import org.estatio.module.numerator.EstatioNumeratorModule;
import org.estatio.module.numerator.fixture.NumeratorModule_setup;
import org.estatio.module.numerator.fixture.NumeratorModule_setupPrereqs;
import org.estatio.module.numerator.fixture.NumeratorModule_tearDown;

/**
 * Base class for integration tests.
 */
public abstract class NumeratorModuleIntegTestAbstract extends IntegrationTestAbstract2 {

    public static class NumeratorAppManifest extends AppManifestAbstract {

        final static Builder BUILDER =
                Builder.forModules(EstatioNumeratorModule.class, SecurityModule.class );

        public NumeratorAppManifest() {
            super(BUILDER);
        }
    }

    @BeforeClass
    public static void initClass() {
        bootstrapUsing(NumeratorAppManifest.BUILDER.withAdditionalModules(FakeDataModule.class));
    }

    @Before
    public void setup() {
        runFixtureScript(new NumeratorModule_setupPrereqs());
        runFixtureScript(new NumeratorModule_setup());
    }

    @After
    public void tearDown() {
        runFixtureScript(new NumeratorModule_tearDown());
    }

    @Inject
    protected FakeDataService fakeDataService;
}

