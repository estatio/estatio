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
package org.estatio.dom.numerator;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class NumeratorTest {

    Numerator numerator;

    @Before
    public void setUp() throws Exception {
        numerator = new Numerator();
        numerator.setFormat("XXX-%05d");
    }


    public static class Increment extends NumeratorTest {

        @Test
        public void happyCase() {
            assertEquals("XXX-00001", numerator.nextIncrementStr());
            assertEquals(BigInteger.ONE, numerator.getLastIncrement());
        }

    }

    public static class Title extends NumeratorTest {

        @Test
        public void whenScoped() {
            numerator.setObjectType("CUS");
            numerator.setObjectIdentifier("123");
            numerator.setLastIncrement(BigInteger.ONE);

            assertThat(numerator.isScoped()).isTrue();

            assertEquals("XXX-00001", numerator.title());
            assertEquals("XXX-00001", numerator.format(BigInteger.ONE));
        }

        @Test
        public void whenGlobal() {
            numerator.setName("Bananas");
            assertThat(numerator.isScoped()).isFalse();

            assertEquals("Bananas", numerator.title());
        }
    }

    public static class Format extends NumeratorTest {
        @Test
        public void happyCase() {
            assertEquals("XXX-00001", numerator.format(BigInteger.ONE));
        }
    }

    public static class AppTenancyPathAdaption extends NumeratorTest {

        @Test
        public void appTenancyPathWithWildCardIsSetToParent() {

            //when
            numerator.setApplicationTenancyPath("/FRA/%/FR03");

            //then
            assertEquals(numerator.adaptedAppPathIfNeeded(), "/FRA");
        }

    }


    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Property.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new Numerator());
        }

    }

}