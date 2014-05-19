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
import org.estatio.fixture.asset.PropertiesAndUnitsForAll;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsForAll;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NumeratorTest_findGlobalNumerator extends EstatioIntegrationTest {


    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
                execute("properties", new PropertiesAndUnitsForAll(), executionContext);
            }
        });
    }

    @Inject
    private Numerators numerators;
    @Inject
    private Properties properties;

    private Property property;
    private Property property2;

    @Before
    public void setUp() throws Exception {
        property = properties.allProperties().get(0);
        property2 = properties.allProperties().get(1);
    }

    @Test
    public void whenExists() throws Exception {

        // given
        numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, property, "ABC-%05d", new BigInteger("10"));
        numerators.createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, property2, "DEF-%05d", new BigInteger("100"));
        numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, "ABC-%05d", new BigInteger("1000"));

        // when
        Numerator in = numerators.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);

        // then
        assertThat(in.getLastIncrement(), is(new BigInteger("1000")));
    }

}
