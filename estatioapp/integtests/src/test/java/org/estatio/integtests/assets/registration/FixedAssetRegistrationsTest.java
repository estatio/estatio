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

package org.estatio.integtests.assets.registration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrations;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class FixedAssetRegistrationsTest extends EstatioIntegrationTest {

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
    FixedAssets fixedAssets;

    @Inject
    FixedAssetRegistrations fixedAssetRegistrations;

    public static class FindBySubject extends FixedAssetRegistrationsTest {

        // TODO: Is this test actually necessary? I cannot figure out where
        // FixedAssetRegistration is actually used

        @Ignore
        @Test
        public void findBySubject() throws Exception {
            // given
            List<FixedAsset> fixedAsset = fixedAssets.matchAssetsByReferenceOrName(_PropertyForOxfGb.REF);
            assertThat(fixedAsset.size(), is(1));

            // when
            List<FixedAssetRegistration> results = fixedAssetRegistrations.findBySubject(fixedAsset.get(0));

            // then
            assertThat(results.size(), is(1));
        }
    }
}
