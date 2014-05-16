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
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.fixturescripts.CreateRetroInvoices;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RetroInvoicesTest extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupDataForClass() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
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
    }

    @Test
    public void step0_dueDates() {
        SortedSet<LocalDate> dueDates = creator.findDueDatesForLease(new LocalDate(2012, 1, 1), new LocalDate(2014, 1, 1), lease);
        assertThat(dueDates.size(), is(10));
    }

    @Test
    public void step1_retroRun() {
        creator.createLease(lease, new LocalDate(2012, 1, 1), new LocalDate(2014, 1, 1), FixtureScript.ExecutionContext.NOOP);
        assertThat(invoices.findInvoices(lease).size(), is(8));
    }

    @Test
    public void step2_caluclate() {
        lease.terminate(new LocalDate(2013, 10, 1), true);
        lease.calculate(InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, new LocalDate(2014, 2, 1), new LocalDate(2012, 1, 1), new LocalDate(2014, 1, 1));
        List<Invoice> invoicesList = invoices.findInvoices(lease);
        assertThat(invoicesList.size(), is(9));
        Invoice invoice = invoicesList.get(8);
        assertThat(invoice.getDueDate(), is(new LocalDate(2014, 2, 1)));
        assertThat(invoice.getGrossAmount(), is(new BigDecimal("-8170.01")));
    }

    @Test
    public void step3_checkContractualRent() throws Exception {
        LeaseItem leaseItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) leaseItem.findTerm(new LocalDate(2012, 1, 1));
        assertThat(term.getContractualRent(), is(new BigDecimal("21058.27")));
    }

    @Test
    public void step4_indexation() throws Exception {

    }

}
