/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.capex.dom.impmgr;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "orders.OrderInvoiceImportMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Projects"
)
public class OrderInvoiceImportMenu {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(hidden = Where.EVERYWHERE) // method is used in integ test
    @MemberOrder(sequence = "2")
    public Blob importOrdersAndInvoicesTestSheet(
            final String sheetName,
            final Blob spreadSheet) {
        final List<OrderInvoiceLine> lines = orderInvoiceImportService.createLines(sheetName, spreadSheet);


        return orderInvoiceImportService.createSheet(lines);
    }

    public String default0ImportOrdersAndInvoicesTestSheet(){
        return "budget travaux";
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public OrderInvoiceSheet importOrdersAndInvoices(
            final String sheetName,
            final Blob spreadSheet) {

        final List<OrderInvoiceLine> lines = orderInvoiceImportService.createLines(sheetName, spreadSheet);

        final OrderInvoiceSheet sheet = factoryService.instantiate(OrderInvoiceSheet.class);
        sheet.setLines(lines);

        return sheet;
    }

    public String default0ImportOrdersAndInvoices(){
        return "budget travaux";
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public OrderInvoiceImportReport reportImportResults(){
        return new OrderInvoiceImportReport();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public SupplierBankAccountImportManager importSellers(){
        return new SupplierBankAccountImportManager();
    }

    @Inject
    private OrderInvoiceImportService orderInvoiceImportService;

    @Inject
    private FactoryService factoryService;

}
