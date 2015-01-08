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
package org.estatio.integtests.charge;

import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;

public class ChargesTest extends EstatioIntegrationTest {

    public static class FindCharge extends ChargesTest {

        @Before
        public void setupData() {
            runScript(new EstatioBaseLineFixture());
        }

        @Inject
        private Charges charges;

        @Test
        public void whenExists() throws Exception {
            // when
            final Charge charge = charges.findByReference(ChargeRefData.IT_RENT);
            // then
            Assert.assertEquals(charge.getReference(), ChargeRefData.IT_RENT);
        }


    }
}