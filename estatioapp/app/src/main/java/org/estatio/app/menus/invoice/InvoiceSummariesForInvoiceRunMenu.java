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
package org.estatio.app.menus.invoice;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForInvoiceRun;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named="Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.1"
)
@DomainObject(editing = Editing.DISABLED)
public class InvoiceSummariesForInvoiceRunMenu extends UdoDomainRepositoryAndFactory<InvoiceSummaryForInvoiceRun> {

    public InvoiceSummariesForInvoiceRunMenu() {
        super(InvoiceSummariesForInvoiceRunMenu.class, InvoiceSummaryForInvoiceRun.class);
    }




    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<InvoiceSummaryForInvoiceRun> allInvoiceRuns() {
        return allInstances();
    }





    @Programmatic
    public InvoiceSummaryForInvoiceRun findByRunId(
            final String runId) {
        return firstMatch("findByRunId",
                "runId", runId);
    }


}
