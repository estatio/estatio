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
package org.estatio.dom.invoice.viewmodel;

import java.util.List;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.invoice.InvoiceStatus;

@DomainService(menuOrder = "10")
@Immutable
public class InvoiceSummariesForPropertyDueDateStatus
        extends EstatioDomainService<InvoiceSummaryForPropertyDueDateStatus> {

    public InvoiceSummariesForPropertyDueDateStatus() {
        super(InvoiceSummariesForPropertyDueDateStatus.class, InvoiceSummaryForPropertyDueDateStatus.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "11")
    public List<InvoiceSummaryForPropertyDueDateStatus> invoicesForStatusNew() {
        return findByStatus(InvoiceStatus.NEW);
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "12")
    public List<InvoiceSummaryForPropertyDueDateStatus> invoicesForStatusApproved() {
        return findByStatus(InvoiceStatus.APPROVED);
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "90")
    public List<InvoiceSummaryForPropertyDueDateStatus> invoicesForPropertyDueDateStatus() {
        return allInstances();
    }
    
    // //////////////////////////////////////

    @Programmatic
    public List<InvoiceSummaryForPropertyDueDateStatus> findByStatus(
            final @Optional InvoiceStatus status) {
        return allMatches("findByStatus",
                "status", status.name());
    }
}
