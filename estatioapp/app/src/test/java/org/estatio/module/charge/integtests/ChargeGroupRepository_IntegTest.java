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
package org.estatio.module.charge.integtests;

import javax.inject.Inject;

import org.junit.Test;

import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeGroupRepository;
import org.estatio.module.charge.fixtures.chargegroups.enums.ChargeGroup_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class ChargeGroupRepository_IntegTest extends ChargeModuleIntegTestAbstract {

    @Inject
    ChargeGroupRepository chargeGroupRepository;

    public static class FindChargeGroup extends ChargeGroupRepository_IntegTest {

        @Test
        public void whenExists() throws Exception {
            ChargeGroup chargeGroup = ChargeGroup_enum.Rent.findUsing(serviceRegistry);
            assertThat(chargeGroup).isNotNull();
        }

    }

    public static class CreateChargeGroup extends ChargeGroupRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            final ChargeGroup chargeGroup = chargeGroupRepository.createChargeGroup("TEST CHARGE GROUP", "Test charge group");
            assertThat(chargeGroup.getReference()).isEqualTo("TEST CHARGE GROUP");
        }
    }
}