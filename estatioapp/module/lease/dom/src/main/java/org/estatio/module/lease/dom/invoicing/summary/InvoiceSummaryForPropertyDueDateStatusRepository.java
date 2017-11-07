/*
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
package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.invoice.InvoiceStatus;

@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceSummaryForPropertyDueDateStatusRepository
        extends UdoDomainRepositoryAndFactory<InvoiceSummaryForPropertyDueDateStatus> {

    public InvoiceSummaryForPropertyDueDateStatusRepository() {
        super(InvoiceSummaryForPropertyDueDateStatusRepository.class, InvoiceSummaryForPropertyDueDateStatus.class);
    }

    @Programmatic
    public List<InvoiceSummaryForPropertyDueDateStatus> findInvoicesByStatus(
            final InvoiceStatus status) {
        return allMatches("findByStatus",
                "status", status);
    }

    @Programmatic
    public List<InvoiceSummaryForPropertyDueDateStatus> findInvoicesByStatusAndDueDateAfter(
            final InvoiceStatus status,
            final LocalDate fromDate) {
        return allMatches("findByStatusAndDueDateAfter",
                "status", status,
                "dueDateAfter", fromDate);
    }

    @Programmatic
    public List<InvoiceSummaryForPropertyDueDateStatus> findByAtPathAndSellerReferenceAndStatus(
                final String atPath,
                final String sellerReference,
                final InvoiceStatus status) {
        return allMatches("findByAtPathAndSellerReferenceAndStatus",
                "atPath", atPath,
                "sellerReference", sellerReference,
                "status", status);
    }

    @Programmatic
    public List<InvoiceSummaryForPropertyDueDateStatus> findByAtPathAndSellerReferenceAndStatusAndDueDate(
                final String atPath,
                final String sellerReference,
                final InvoiceStatus status,
                final LocalDate dueDate) {
        return allMatches("findByAtPathAndSellerReferenceAndStatusAndDueDate",
                "atPath", atPath,
                "sellerReference", sellerReference,
                "status", status,
                "dueDate", dueDate);
    }

}
