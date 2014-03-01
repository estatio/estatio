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

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioDomainService;

@Immutable
public class InvoiceSummariesForInvoiceRun extends EstatioDomainService<InvoiceSummaryForInvoiceRun> {

    public InvoiceSummariesForInvoiceRun() {
        super(InvoiceSummariesForInvoiceRun.class, InvoiceSummaryForInvoiceRun.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "10")
    public List<InvoiceSummaryForInvoiceRun> invoicesForInvoiceRun() {
        return allInstances();
    }

}
