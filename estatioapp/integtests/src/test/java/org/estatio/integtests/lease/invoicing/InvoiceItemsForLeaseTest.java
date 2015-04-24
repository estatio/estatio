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
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.integtests.EstatioIntegrationTest;

public class InvoiceItemsForLeaseTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
            }
        });
    }

    @Before
    public void setUp() {
        lease = leases.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
    }

    @Inject
    Leases leases;

    @Inject
    InvoiceItemsForLease invoiceItemsForLease;

    @Inject
    LeaseTerms leaseTerms;

    @Inject
    Properties properties;

    Lease lease;

    public static class FindByLeaseTermAndInterval extends InvoiceItemsForLeaseTest {

        @Test
        public void findByLeaseTermAndInterval() throws Exception {
            // given
            LeaseTerm term = leaseTerms.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());
            LocalDateInterval interval = LocalDateInterval.excluding(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));

            // when
            Lease lease = leases.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
            List<InvoiceItemForLease> invoiceItems = invoiceItemsForLease.findByLeaseTermAndInterval(term, interval);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }

    public static class FindByLeaseTermAndIntervalAndInvoiceStatus extends InvoiceItemsForLeaseTest {

        @Test
        public void findByLeaseTermAndIntervalAndInvoiceStatus() throws Exception {
            // given
            LeaseTerm term = leaseTerms.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());
            LocalDateInterval interval = LocalDateInterval.excluding(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemsForLease.findByLeaseTermAndIntervalAndInvoiceStatus(term, interval, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }

    public static class FindByLeaseAndInvoiceStatus extends InvoiceItemsForLeaseTest {

        @Test
        public void findByLeaseAndInvoiceStatus() throws Exception {
            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemsForLease.findByLeaseAndInvoiceStatus(lease, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(2));
        }
    }

    public static class FindByLeaseItemAndInvoiceStatus extends InvoiceItemsForLeaseTest {

        @Test
        public void findByLeaseItemAndInvoiceStatus() throws Exception {
            // given
            LeaseItem leaseItem = lease.findItemsOfType(LeaseItemType.RENT).get(0);

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemsForLease.findByLeaseItemAndInvoiceStatus(leaseItem, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(2));
        }
    }

    public static class FindByLeaseTermAndInvoiceStatus extends InvoiceItemsForLeaseTest {

        @Test
        public void findByLeaseTermAndInvoiceStatus() throws Exception {
            // given
            LeaseTerm term = leaseTerms.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemsForLease.findByLeaseTermAndInvoiceStatus(term, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }
}
