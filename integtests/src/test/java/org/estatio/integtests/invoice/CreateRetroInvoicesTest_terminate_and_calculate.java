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

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.*;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.fixturescripts.CreateRetroInvoices;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CreateRetroInvoicesTest_terminate_and_calculate extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
                execute("properties", new PropertiesAndUnitsFixture(), executionContext);
                execute("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(), executionContext);
                execute("invoices", new InvoiceAndInvoiceItemFixture(), executionContext);
            }
        });
    }

    private Invoices invoices;
    private Properties properties;
    private Leases leases;
    private CreateRetroInvoices creator;
    private InvoiceCalculationService invoiceCalculationService;

    Lease lease;

    @Before
    public void setup() {
        invoices = service(Invoices.class);
        properties = service(Properties.class);
        leases = service(Leases.class);
        invoiceCalculationService = service(InvoiceCalculationService.class);

        creator = new CreateRetroInvoices();
        creator.leases = leases;
        creator.invoices = invoices;
        creator.properties = properties;
        creator.invoiceCalculationService = invoiceCalculationService;

        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");

        SortedSet<LocalDate> dueDates = creator.findDueDatesForLease(dt(2012, 1, 1), dt(2014, 1, 1), lease);
        assertThat(dueDates.size(), is(10));
    }

    @Test
    public void step1_retroRun() {
        // when
        creator.createLease(lease, dt(2012, 1, 1), dt(2014, 1, 1), FixtureScript.ExecutionContext.NOOP);

        // then
        assertThat(invoices.findInvoices(lease).size(), is(8));

        // and given
        lease.terminate(dt(2013, 10, 1), true);

        // when
        lease.calculate(InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, dt(2014, 2, 1), dt(2012, 1, 1), dt(2014, 1, 1));

        // then
        List<Invoice> invoicesList = invoices.findInvoices(lease);
        assertThat(invoicesList.size(), is(9));
        Invoice invoice = invoicesList.get(8);
        assertThat(invoice.getDueDate(), is(dt(2014, 2, 1)));
        assertThat(invoice.getGrossAmount(), is(bd("-8170.01")));

        // and also
        LeaseItem leaseItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) leaseItem.findTerm(dt(2012, 1, 1));
        assertThat(term.getContractualRent(), is(bd("21058.27")));
    }


}
