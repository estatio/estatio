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

import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

public class ChargesTest extends EstatioIntegrationTest {

    public static class FindCharge extends ChargesTest {

        @Before
        public void setupData() {
            runFixtureScript(new EstatioBaseLineFixture());
        }

        @Inject
        private ChargeRepository chargeRepository;

        @Test
        public void whenExists() throws Exception {
            // when
            final Charge charge = chargeRepository.findByReference(ChargeRefData.IT_RENT);
            // then
            Assert.assertEquals(charge.getReference(), ChargeRefData.IT_RENT);
        }
    }

    public static class ChargesForCountry extends ChargesTest {

        private List<Charge> gbCharges;

        @Before
        public void setupData() {
            runScript(new EstatioBaseLineFixture());
        }

        @Inject
        private ChargeRepository chargeRepository;

        @Before
        public void setUp() throws Exception {

            gbCharges = Lists.newArrayList(
                    chargeRepository.findByReference(ChargeRefData.GB_DISCOUNT),
                    chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE),
                    chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET1),
                    chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET2),
                    chargeRepository.findByReference(ChargeRefData.GB_RENT),
                    chargeRepository.findByReference(ChargeRefData.GB_ENTRY_FEE),
                    chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE_INDEXABLE),
                    chargeRepository.findByReference(ChargeRefData.GB_TAX),
                    chargeRepository.findByReference(ChargeRefData.GB_TURNOVER_RENT),
                    chargeRepository.findByReference(ChargeRefData.GB_PERCENTAGE),
                    chargeRepository.findByReference(ChargeRefData.GB_DEPOSIT));

        }

        @Test
        public void forGlobal() throws Exception {

            // expect
            expectedExceptions.expect(IllegalArgumentException.class);

            // when
            chargeRepository.chargesForCountry("/");
        }

        @Test
        public void forCountry() throws Exception {
            // when
            final List<Charge> chargeList = chargeRepository.chargesForCountry("/"+CountriesRefData.GBR);

            // then
            Assertions.assertThat(chargeList).containsOnly(gbCharges.toArray(new Charge[gbCharges.size()]));
        }

        @Test
        public void forProperty() throws Exception {
            // when
            final List<Charge> chargeList = chargeRepository.chargesForCountry("/"+CountriesRefData.GBR+"/OXF");

            // then
            Assertions.assertThat(chargeList).containsOnly(gbCharges.toArray(new Charge[gbCharges.size()]));
        }

        @Test
        public void forLocal() throws Exception {
            // when
            final List<Charge> chargeList = chargeRepository.chargesForCountry("/"+CountriesRefData.GBR+"/OXF/ta");

            // then
            Assertions.assertThat(chargeList).containsOnly(gbCharges.toArray(new Charge[gbCharges.size()]));
        }

    }

}