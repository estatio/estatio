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
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class FixedAssetsTest extends EstatioIntegrationTest {

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
    FixedAssets fixedAssets;

    public static class AutoComplete extends FixedAssetsTest {

        @Test
        public void whenPresent() throws Exception {
            Assert.assertThat(fixedAssets.autoComplete("mall").size(), Is.is(1));
        }

        @Test
        public void whenNotPresent() throws Exception {
            Assert.assertThat(fixedAssets.autoComplete("nonExistent").size(), Is.is(0));
        }

    }

    public static class MatchAssetsByReferenceOrName extends FixedAssetsTest {

        @Test
        public void whenPresent() throws Exception {
            Assert.assertThat(fixedAssets.matchAssetsByReferenceOrName("*mall*").size(), Is.is(1));
        }

        @Test
        public void whenNotPresent() throws Exception {
            Assert.assertThat(fixedAssets.matchAssetsByReferenceOrName("*nonExistent*").size(), Is.is(0));
        }
    }

}