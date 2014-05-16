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
package org.estatio.integration.tests.numerator;

import java.math.BigInteger;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.numerator.Numerators;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NumeratorTest_finders extends EstatioIntegrationTest {

    private Numerators numerators;
    private Properties properties;
    private Property property;
    private Property property2;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    @Before
    public void setUp() throws Exception {
        numerators = service(Numerators.class);
        properties = service(Properties.class);
        property = properties.allProperties().get(0);
        property2 = properties.allProperties().get(1);
        numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, property, "ABC-%05d", new BigInteger("10"));
        numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, property2, "DEF-%05d", new BigInteger("100"));
        numerators.createScopedNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, property, "ABC-%05d", new BigInteger("1000"));
    }

    @Test
    public void numerator_increment() throws Exception {

    }

}
