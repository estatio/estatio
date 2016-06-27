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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.invoice.viewmodel.InvoiceImportLine;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        named = "Import manager for invoice for lease",
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class InvoiceImportManager extends EstatioViewModel {

    public String title() {
        return "Import manager for invoice for lease";
    }

    public InvoiceImportManager() {
        this.name = "Invoice Import";
    }

    @Action
    @ActionLayout(cssClassFa = "fa-upload")
    @CollectionLayout(paged = -1)
    public List<InvoiceImportLine> importInvoices(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<InvoiceImportLine> lineItems =
                excelService.fromExcel(spreadsheet, InvoiceImportLine.class);
        return lineItems;
    }

    @Getter @Setter
    private String name;

    @Inject
    private ExcelService excelService;

}
