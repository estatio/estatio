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
package org.estatio.dom.charge;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.tax.dom.Tax;

public class Charge_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(ChargeGroup.class))
                    .withFixture(pojos(Tax.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .withFixture(pojos(Charge.class))
                    .exercise(new Charge());
        }

    }

    public static class Reference4  {

        @Test
        public void happy_case() {
            final Charge charge = Charge.builder().reference("XX1234").build();

            Assertions.assertThat(charge.getReference4()).isEqualTo("1234");
        }

        @Test
        public void too_short() {
            final Charge charge = Charge.builder().reference("XX123").build();

            Assertions.assertThat(charge.getReference4()).isNull();
        }

        @Test
        public void longer_than_required() {
            final Charge charge = Charge.builder().reference("XX12345678").build();

            Assertions.assertThat(charge.getReference4()).isEqualTo("1234");
        }

    }

    public static class Reference4i {

        @Test
        public void happy_case() {
            final Charge charge = Charge.builder().reference("XX1234").build();

            Assertions.assertThat(charge.getReference4i()).isEqualTo(1234);
        }

        @Test
        public void too_short() {
            final Charge charge = Charge.builder().reference("XX123").build();

            Assertions.assertThat(charge.getReference4i()).isNull();
        }

        @Test
        public void longer_than_required() {
            final Charge charge = Charge.builder().reference("XX12345678").build();

            Assertions.assertThat(charge.getReference4i()).isEqualTo(1234);
        }

        @Test
        public void does_not_parse() {
            final Charge charge = Charge.builder().reference("XX123a").build();

            Assertions.assertThat(charge.getReference4i()).isNull();
        }

    }


}