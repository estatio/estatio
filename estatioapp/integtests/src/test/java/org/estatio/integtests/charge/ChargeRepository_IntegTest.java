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

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.estatio.charge.dom.Charge;
import org.estatio.charge.dom.ChargeRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.incode.module.country.fixture.CountriesRefData;
import org.estatio.integtests.EstatioIntegrationTest;

public class ChargeRepository_IntegTest extends EstatioIntegrationTest {

    public static class FindCharge extends ChargeRepository_IntegTest {

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

    public static class ChargeRepositoryForCountry extends ChargeRepository_IntegTest {

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
                    chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE2),
                    chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_1),
                    chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_2),
                    chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_3),
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
            final List<Charge> chargeList = chargeRepository.chargesForCountry("/" + CountriesRefData.GBR);

            // then
            Assertions.assertThat(chargeList).containsOnly(gbCharges.toArray(new Charge[gbCharges.size()]));
        }

        @Test
        public void forProperty() throws Exception {
            // when
            final List<Charge> chargeList = chargeRepository.chargesForCountry("/" + CountriesRefData.GBR + "/OXF");

            // then
            Assertions.assertThat(chargeList).containsOnly(gbCharges.toArray(new Charge[gbCharges.size()]));
        }

        @Test
        public void forLocal() throws Exception {
            // when
            final List<Charge> chargeList = chargeRepository.chargesForCountry("/" + CountriesRefData.GBR + "/OXF/ta");

            // then
            Assertions.assertThat(chargeList).containsOnly(gbCharges.toArray(new Charge[gbCharges.size()]));
        }

    }

}