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
package org.estatio.module.lease.integtests.invoicing.summary;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRun;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRunRepository;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceSummaryRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class FindInvoicesByStatus extends InvoiceSummaryRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext ec) {

                    ec.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb,
                            InvoiceForLease_enum.KalPoison001Nl);
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
                    executionContext.executeChild(this, Lease_enum.KalPoison001Nl.builder());
                }
            });
        }

        @Test
        public void find_is_successful() {
            // given
            Lease lease = Lease_enum.KalPoison001Nl.findUsing(serviceRegistry);
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
            final InvoiceSummaryForInvoiceRun invoiceSummary = invoiceSummaryForInvoiceRunRepository.findByRunId(interactionId);
            assertThat(invoiceSummary.getRunId()).isEqualTo(interactionId);
            assertThat(invoiceSummary.getAtPath()).isEqualTo(lease.getApplicationTenancy().getPath());
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