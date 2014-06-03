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
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.Numerators;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NumeratorTest_increment extends EstatioIntegrationTest {


    private Numerator scopedNumerator;
    private Numerator scopedNumerator2;
    private Numerator globalNumerator;

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PropertyForOxf(), executionContext);
                execute(new PropertyForKal(), executionContext);
            }
        });
    }

    @Inject
    private Numerators numerators;
    @Inject
    private Properties properties;

    private Property propertyOxf;
    private Property propertyKal;

    @Before
    public void setUp() throws Exception {
        propertyOxf = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
        propertyKal = properties.findPropertyByReference(PropertyForKal.PROPERTY_REFERENCE);

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
        assertThat(scopedNumerator.increment(), is("ABC-00011"));

        // then
        assertThat(scopedNumerator.getLastIncrement(), is(new BigInteger("11")));
    }

    @Test
    public void forGlobalNumerator() throws Exception {

        // givem
        //globalNumerator = numerators.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
        assertThat(globalNumerator.getLastIncrement(), is(new BigInteger("1000")));

        // when
        assertThat(globalNumerator.increment(), is("ABC-01001"));

        // then
        assertThat(globalNumerator.getLastIncrement(), is(new BigInteger("1001")));
    }

}
