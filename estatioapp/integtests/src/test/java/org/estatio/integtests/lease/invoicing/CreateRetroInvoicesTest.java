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
package org.estatio.integtests.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceService;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixturescripts.CreateRetroInvoices;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

public class CreateRetroInvoicesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
    }

    @Inject
    Invoices invoices;

    @Inject
    Properties properties;

    @Inject
    Leases leases;

    @Inject
    InvoiceCalculationService invoiceCalculationService;

    @Inject
    InvoiceService invoiceService;

    CreateRetroInvoices creator;

    Lease lease;

    @Before
    public void setup() {
        creator = new CreateRetroInvoices();
        creator.leases = leases;
        creator.invoices = invoices;
        creator.properties = properties;
        creator.invoiceCalculationService = invoiceCalculationService;

        lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
    }

    public static class FindDueDatesForLease extends CreateRetroInvoicesTest {

        @Test
        public void whenPresent() {
            // when
            SortedSet<LocalDate> dueDates = creator.findDueDatesForLease(VT.ld(2012, 1, 1), VT.ld(2014, 1, 1), lease);
            // then
            assertThat(dueDates.size(), is(10));
        }

    }

    public static class Terminate_and_Calculate extends CreateRetroInvoicesTest {

        @Test
        public void step1_retroRun() {
            // given
            SortedSet<LocalDate> dueDates = creator.findDueDatesForLease(VT.ld(2012, 1, 1), VT.ld(2014, 1, 1), lease);
            assertThat(dueDates.size(), is(10));

            // when
            creator.createLease(lease, VT.ld(2012, 1, 1), VT.ld(2014, 1, 1), FixtureScript.ExecutionContext.NOOP);

            // then
            assertThat(invoices.findInvoices(lease).size(), is(8));

            // and given
            lease.terminate(VT.ld(2013, 10, 1), true);

            // when
            invoiceService.calculate(lease, InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, VT.ld(2014, 2, 1), VT.ld(2012, 1, 1), VT.ld(2014, 1, 1));

            // then
            List<Invoice> invoicesList = invoices.findInvoices(lease);
            assertThat(invoicesList.size(), is(9));
            Invoice invoice = invoicesList.get(8);
            assertThat(invoice.getDueDate(), is(VT.ld(2014, 2, 1)));
            assertThat(invoice.getGrossAmount(), is(VT.bd("-8170.01")));

            // and also
            LeaseItem leaseItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
            LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) leaseItem.findTerm(VT.ld(2012, 1, 1));
            assertThat(term.getContractualRent(), is(VT.bd("21058.27")));
        }
    }

}