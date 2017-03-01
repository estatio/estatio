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
package org.estatio.app.services.invoice;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.dom.asset.Property;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.services.invoice.InvoiceImportMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices"
)
public class InvoiceImportMenu {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )

    @MemberOrder(sequence = "1")
    public InvoiceImportManager importInvoicesForLease(final Property property) {
        return new InvoiceImportManager(property);
    }

    @Inject
    private ExcelService excelService;

}
