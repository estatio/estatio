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
package org.estatio.app.services.order;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.order.OrderInvoiceImportManager"
)
public class OrderInvoiceImportManager {

    public OrderInvoiceImportManager() {
    }

    public String title() {
        return "Order - Invoice Import";
    }

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public List<OrderInvoiceImportLine> importExcelSheet(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        return orderInvoiceImportService.createLines(spreadsheet);
    }

    @Inject
    private DomainObjectContainer container;

    @Inject
    private OrderInvoiceImportService orderInvoiceImportService;

}
