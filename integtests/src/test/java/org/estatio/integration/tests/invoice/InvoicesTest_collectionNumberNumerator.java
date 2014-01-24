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
package org.estatio.integration.tests.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.numerator.Numerator;
import org.estatio.fixture.EstatioTransactionalObjectsTeardownFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class InvoicesTest_collectionNumberNumerator extends EstatioIntegrationTest {

    private Invoices invoices;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsTeardownFixture());
    }

    @Before
    public void setUp() throws Exception {
        invoices = service(Invoices.class);
    }
    
    @Test
    public void findWhenNone() throws Exception {
        Numerator numerator = invoices.findCollectionNumberNumerator();
        Assert.assertNull(numerator);
    }

    @Test
    public void createThenFind() throws Exception {
        Numerator numerator = invoices.createCollectionNumberNumerator("%09d", BigInteger.TEN);
        Assert.assertNotNull(numerator);
        
        assertThat(numerator.getName(), is(Constants.COLLECTION_NUMBER_NUMERATOR_NAME));
        assertThat(numerator.getObjectType(), is(nullValue()));
        assertThat(numerator.getObjectIdentifier(), is(nullValue()));
        assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
    }
    
}
