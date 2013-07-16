/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integration.tests.assets;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.asset.FixedAssets;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class FixedAssetsTest_finders extends EstatioIntegrationTest {

    private FixedAssets fixedAssets;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }
    
    @Before
    public void setUp() throws Exception {
        fixedAssets = service(FixedAssets.class);
    }
    
    @Test
    public void findAssetsByReferenceOrName() throws Exception {
        Assert.assertThat(fixedAssets.findAssetsByReferenceOrName("*mall*").size(), Is.is(1));
    }

    @Test
    public void autoComplete() throws Exception {
        Assert.assertThat(fixedAssets.autoComplete("mall").size(), Is.is(1));
    }



}
