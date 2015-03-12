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
package org.estatio.integtests.app;

import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForPropertyDueDate;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDate;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class InvoiceSummariesTest extends EstatioIntegrationTest {

    public static class InvoicesForPropertyDueDate extends InvoiceSummariesTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                }
            });
        }

        @Inject
        private InvoiceSummariesForPropertyDueDate invoiceSummaries;

        @Test
        public void whenPresent() throws Exception {
            final List<InvoiceSummaryForPropertyDueDate> summaries =
                    invoiceSummaries.allInvoicesByPropertyDueDate();
            assertThat(summaries.size(), is(2));
            InvoiceSummaryForPropertyDueDate summary = summaries.get(0);
            Property property = summary.getProperty();
            assertThat(property, is(not(nullValue())));
        }

    }

}
