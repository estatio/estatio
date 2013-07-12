/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.estatio.integration.EstatioIntegrationTest;

@FixMethodOrder()
public class NumeratorIntegrationTest extends EstatioIntegrationTest {

    @Before
    public void setUp() throws Exception {
        scenarioExecution.service(Numerators.class).establishNumerator(NumeratorType.INVOICE_NUMBER);
    }

    @Test
    public void t01_numeratorCanBeFound() throws Exception {
        Numerator numerator = scenarioExecution.service(Numerators.class).findNumeratorByType(NumeratorType.INVOICE_NUMBER);
        assertNotNull(numerator);
    }

    @Test
    public void t02_canFindUsingNaiveImpl() throws Exception {
        assertThat(scenarioExecution.service(Numerators.class).allNumerators().size(), is(1));
    }

    @Test
    public void t03_numberOfNumeratorsIsOne() throws Exception {
        Numerator in = scenarioExecution.service(Numerators.class).findNumeratorByType(NumeratorType.INVOICE_NUMBER);
        assertThat(in.getLastIncrement(), is(BigInteger.ZERO));
        assertThat(in.increment(), is(BigInteger.ONE));
        assertThat(in.getLastIncrement(), is(BigInteger.ONE));
    }

}
