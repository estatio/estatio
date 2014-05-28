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
package org.estatio.integtests.invoice;

import javax.inject.Inject;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.numerator.Numerator;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.PersonForLinusTorvalds;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class InvoicesTest_findInvoiceNumberNumerator extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PersonForLinusTorvalds(), executionContext);

                execute(new PropertyForOxf(), executionContext);
                execute(new PropertyForKal(), executionContext);

                execute(new LeaseBreakOptionsForOxfTopModel001(), executionContext);

                execute(new LeaseBreakOptionsForOxfMediax002(), executionContext);

                execute(new LeaseBreakOptionsForOxfPoison003(), executionContext);

                execute(new LeaseForOxfPret004(), executionContext);

                execute(new LeaseItemAndTermsForOxfMiracl005(), executionContext);
            }
        });
    }

    @Inject
    private Invoices invoices;
    @Inject
    private Properties properties;

    private Property propertyOxf;

    @Test
    public void whenNone() throws Exception {
        // given
        propertyOxf = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);

        // when
        Numerator numerator = invoices.findInvoiceNumberNumerator(propertyOxf);
        // then
        Assert.assertNull(numerator);
    }


}
