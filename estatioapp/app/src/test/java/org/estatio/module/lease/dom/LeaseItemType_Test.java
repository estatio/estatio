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
package org.estatio.module.lease.dom;

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseItemType_Test {

    @Test
    public void typesForLeaseTermForServiceCharge_Test(){

        // given
        // when
        List<LeaseItemType> typesForServiceChargeFound = LeaseItemType.typesForLeaseTermForServiceCharge();

        // then
        assertThat(typesForServiceChargeFound.size()).isEqualTo(3);
        assertThat(typesForServiceChargeFound.get(0)).isEqualTo(LeaseItemType.SERVICE_CHARGE);
    }

}