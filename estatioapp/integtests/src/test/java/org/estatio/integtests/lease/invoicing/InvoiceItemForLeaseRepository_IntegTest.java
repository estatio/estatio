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

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemForLeaseRepository;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoiceItemForLeaseRepository_IntegTest extends EstatioIntegrationTest {

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
        lease = leaseRepository.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    Lease lease;

    public static class FindByLeaseRepositoryTerm extends InvoiceItemForLeaseRepository_IntegTest {

        @Test
        public void findByLeaseTerm() throws Exception {
            // given
            LeaseTerm term = leaseTermRepository.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemForLeaseRepository.findByLeaseTerm(term);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }

    public static class FindByLeaseRepositoryTermAndInterval extends InvoiceItemForLeaseRepository_IntegTest {

        @Test
        public void findByLeaseTermAndInterval() throws Exception {
            // given
            LeaseTerm term = leaseTermRepository.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());
            LocalDateInterval interval = LocalDateInterval.excluding(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));

            // when
            Lease lease = leaseRepository.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
            List<InvoiceItemForLease> invoiceItems = invoiceItemForLeaseRepository.findByLeaseTermAndInterval(term, interval);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }

    public static class FindByLeaseTermAndIntervalAndInvoiceStatusRepository extends
            InvoiceItemForLeaseRepository_IntegTest {

        @Test
        public void findByLeaseTermAndIntervalAndInvoiceStatus() throws Exception {
            // given
            LeaseTerm term = leaseTermRepository.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());
            LocalDateInterval interval = LocalDateInterval.excluding(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemForLeaseRepository.findByLeaseTermAndIntervalAndInvoiceStatus(term, interval, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }

    public static class FindByLeaseAndInvoiceStatusRepository extends InvoiceItemForLeaseRepository_IntegTest {

        @Test
        public void findByLeaseAndInvoiceStatus() throws Exception {
            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemForLeaseRepository.findByLeaseAndInvoiceStatus(lease, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(2));
        }
    }

    public static class FindByLeaseItemAndInvoiceStatusRepository extends InvoiceItemForLeaseRepository_IntegTest {

        @Test
        public void findByLeaseItemAndInvoiceStatus() throws Exception {
            // given
            LeaseItem leaseItem = lease.findItemsOfType(LeaseItemType.RENT).get(0);

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemForLeaseRepository.findByLeaseItemAndInvoiceStatus(leaseItem, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(2));
        }
    }

    public static class FindByLeaseTermAndInvoiceStatusRepository extends InvoiceItemForLeaseRepository_IntegTest {

        @Test
        public void findByLeaseTermAndInvoiceStatus() throws Exception {
            // given
            LeaseTerm term = leaseTermRepository.findByLeaseItemAndStartDate(lease.findItemsOfType(LeaseItemType.RENT).get(0), lease.getStartDate());

            // when
            List<InvoiceItemForLease> invoiceItems = invoiceItemForLeaseRepository.findByLeaseTermAndInvoiceStatus(term, InvoiceStatus.NEW);

            // then
            assertThat(invoiceItems.size(), is(1));
        }
    }
}
