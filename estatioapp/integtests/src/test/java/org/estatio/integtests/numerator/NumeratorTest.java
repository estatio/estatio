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
package org.estatio.integtests.numerator;

import java.math.BigInteger;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.Numerators;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NumeratorTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new _PropertyForOxfGb());
                executionContext.executeChild(this, new PropertyForKalNl());
            }
        });
    }

    @Inject
    Numerators numerators;
    @Inject
    Properties properties;

    Property propertyOxf;
    Property propertyKal;

    @Before
    public void setUp() throws Exception {
        propertyOxf = properties.findPropertyByReference(_PropertyForOxfGb.REF);
        propertyKal = properties.findPropertyByReference(PropertyForKalNl.REF);
    }


    public static class AllNumerators extends NumeratorTest {

        @Test
        public void whenExist() throws Exception {

            numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"));
            numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"));
            numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"));

            assertThat(numerators.allNumerators().size(), is(3));
        }

    }

    public static class FindGlobalNumerator extends NumeratorTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"));
            numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"));
            numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"));

            // when
            Numerator in = numerators.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);

            // then
            assertThat(in.getLastIncrement(), is(new BigInteger("1000")));
        }

    }

    public static class FindScopedNumerator extends NumeratorTest {

        @Test
        public void whenExists() throws Exception {

            // given
            numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"));
            numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"));
            numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"));

            // when
            Numerator in = numerators.findScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf);

            // then
            assertThat(in.getLastIncrement(), is(new BigInteger("10")));
        }

    }

    public static class Increment extends NumeratorTest {

        private Numerator scopedNumerator;
        private Numerator scopedNumerator2;
        private Numerator globalNumerator;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            scopedNumerator = numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyOxf, "ABC-%05d", new BigInteger("10"));
            scopedNumerator2 = numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, propertyKal, "DEF-%05d", new BigInteger("100"));
            globalNumerator = numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"));
        }

        @Test
        public void forScopedNumerator() throws Exception {

            // given
            //Numerator scopedNumerator = numerators.findScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, property);
            assertThat(scopedNumerator.getLastIncrement(), is(new BigInteger("10")));

            // when
            assertThat(scopedNumerator.nextIncrementStr(), is("ABC-00011"));

            // then
            assertThat(scopedNumerator.getLastIncrement(), is(new BigInteger("11")));
        }

        @Test
        public void forGlobalNumerator() throws Exception {

            // givem
            //globalNumerator = numerators.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
            assertThat(globalNumerator.getLastIncrement(), is(new BigInteger("1000")));

            // when
            assertThat(globalNumerator.nextIncrementStr(), is("ABC-01001"));

            // then
            assertThat(globalNumerator.getLastIncrement(), is(new BigInteger("1001")));
        }

    }

}