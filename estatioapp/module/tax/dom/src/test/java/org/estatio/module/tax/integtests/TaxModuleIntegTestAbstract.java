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
package org.estatio.module.tax.integtests;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import org.apache.isis.applib.AppManifestAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.security.SecurityModule;

import org.incode.module.country.dom.CountryModule;

import org.estatio.module.tax.EstatioTaxModule;
import org.estatio.module.tax.fixtures.TaxModule_setup;
import org.estatio.module.tax.fixtures.TaxModule_setupPrereqs;
import org.estatio.module.tax.fixtures.TaxModule_tearDown;

public abstract class TaxModuleIntegTestAbstract extends IntegrationTestAbstract2 {

    public static class TaxAppManifest extends AppManifestAbstract {

        final static Builder BUILDER =
                Builder.forModules(CountryModule.class, EstatioTaxModule.class , SecurityModule.class);

        public TaxAppManifest() {
            super(BUILDER);
        }
    }

    @BeforeClass
    public static void initClass() {
        bootstrapUsing(TaxAppManifest.BUILDER.withAdditionalModules(FakeDataModule.class));
    }

    @Before
    public void setup() {
        runFixtureScript(new TaxModule_setupPrereqs());
        runFixtureScript(new TaxModule_setup());
    }

    @After
    public void tearDown() {
        runFixtureScript(new TaxModule_tearDown());
    }

    @Inject
    protected FakeDataService fakeDataService;

}