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

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceSummaryforPropertyDueDateStatus_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class LastInvoiceNumberIntegTest extends
            InvoiceSummaryforPropertyDueDateStatus_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext ec) {
                    ec.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb);
                }
            });
        }

        @Test
        public void happy_case() throws Exception {

            // given
            InvoiceSummaryForPropertyDueDateStatus summary = findSummary(InvoiceStatus.NEW);

            // when
            String lastInvoiceNumberBefore = summary.getLastInvoiceNumber();
            InvoiceForLease invoice = summary.getInvoices().get(0);
            Invoice invoiceApproved = mixin(InvoiceForLease._approve.class, invoice).$$();
            Invoice invoiceInvoiced = mixin(InvoiceForLease._invoice.class, invoiceApproved).$$(invoiceApproved.getInvoiceDate());
            summary = findSummary(InvoiceStatus.INVOICED);
            String lastInvoiceNumberAfter = summary.getLastInvoiceNumber();

            // then
            assertThat(lastInvoiceNumberBefore).isEqualTo("OXF-0000");
            assertThat(lastInvoiceNumberAfter).isEqualTo("OXF-0001");
            assertThat(lastInvoiceNumberAfter).isEqualTo(invoiceInvoiced.getInvoiceNumber());

        }

    }


    InvoiceSummaryForPropertyDueDateStatus findSummary(final InvoiceStatus status) {

        // clears out queryResultsCache
        transactionService.nextTransaction();

        List<InvoiceSummaryForPropertyDueDateStatus> summaries = invoiceSummaryRepository.findInvoicesByStatus(status);
        assertThat(summaries).hasSize(1);
        return summaries.get(0);
    }

    @Inject
    InvoiceSummaryForPropertyDueDateStatusRepository invoiceSummaryRepository;

}
