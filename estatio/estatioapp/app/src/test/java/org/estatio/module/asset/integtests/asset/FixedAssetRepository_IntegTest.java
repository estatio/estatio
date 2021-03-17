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
package org.estatio.module.asset.integtests.asset;

import javax.inject.Inject;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.FixedAssetRepository;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;

public class FixedAssetRepository_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.KalNl.builder());
            }
        });
    }

    @Inject
    FixedAssetRepository fixedAssetRepository;

    public static class AutoComplete extends FixedAssetRepository_IntegTest {

        @Test
        public void whenPresent() throws Exception {
            Assert.assertThat(fixedAssetRepository.autoComplete("mall").size(), Is.is(1));
        }

        @Test
        public void whenNotPresent() throws Exception {
            Assert.assertThat(fixedAssetRepository.autoComplete("nonExistent").size(), Is.is(0));
        }

    }

    public static class MatchAssetRepositoryByReferenceOrName extends FixedAssetRepository_IntegTest {

        @Test
        public void whenPresent() throws Exception {
            Assert.assertThat(fixedAssetRepository.matchAssetsByReferenceOrName("*mall*").size(), Is.is(1));
        }

        @Test
        public void whenNotPresent() throws Exception {
            Assert.assertThat(fixedAssetRepository.matchAssetsByReferenceOrName("*nonExistent*").size(), Is.is(0));
        }
    }

}