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
package org.estatio.asset.dom.registration;

import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.asset.dom.FixedAsset;
import org.estatio.asset.dom.FixedAssetForTesting;

public class LandRegister_Test extends AbstractBeanPropertiesTest {

    public static class BeanProperties extends LandRegister_Test {

        @Test
        public void test() {
            final LandRegister pojo = new LandRegister();
            newPojoTester()
                    .withFixture(pojos(FixedAsset.class, FixedAssetForTesting.class))
                    .withFixture(pojos(FixedAssetRegistration.class, FixedAssetRegistrationForTesting.class))
                    .withFixture(pojos(FixedAssetRegistrationType.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(pojo);
        }
    }

}
