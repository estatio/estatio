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
package org.estatio.integtests.invoice.viewmodel;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.InvoiceForLeaseRepository;
import org.estatio.dom.lease.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForInvoiceRun;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForInvoiceRunRepository;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseForKalPoison001Nl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceSummaryRepository_IntegTest extends EstatioIntegrationTest {

    public static class FindInvoicesByStatus extends InvoiceSummaryRepository_IntegTest {

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
        private InvoiceSummaryForPropertyDueDateStatusRepository repository;

        @Test
        public void happy_case() throws Exception {
            // Given, When
            final List<InvoiceSummaryForPropertyDueDateStatus> summaries = repository.findInvoicesByStatus(InvoiceStatus.NEW);

            // Then
            assertThat(summaries.size()).isEqualTo(2);
        }

    }

    public static class FindByRunId extends InvoiceSummaryRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForKalPoison001Nl());
                }
            });
        }

        @Test
        public void find_is_successful() {
            // given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForKalPoison001Nl.REF);
            final String interactionId = "12345678";
            final InvoiceForLease invoiceForLease = invoiceForLeaseRepository.newInvoice(
                    lease.getApplicationTenancy(),
                    lease.getPrimaryParty(),
                    lease.getSecondaryParty(),
                    PaymentMethod.DIRECT_DEBIT,
                    null,
                    new LocalDate(2010, 1, 1),
                    lease,
                    interactionId);
            invoiceItemForLeaseRepository.newInvoiceItem(invoiceForLease, invoiceForLease.getDueDate());

            //when, then
            final List<InvoiceSummaryForInvoiceRun> invoiceSummaryForInvoiceRuns = invoiceSummaryForInvoiceRunRepository.allInvoiceRuns();
            assertThat(invoiceSummaryForInvoiceRunRepository.findByRunId(interactionId).getRunId()).isEqualTo(interactionId);
        }
    }

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    InvoiceSummaryForInvoiceRunRepository invoiceSummaryForInvoiceRunRepository;

}