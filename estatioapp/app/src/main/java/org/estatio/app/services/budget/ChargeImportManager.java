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
package org.estatio.app.services.budget;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.ViewModelLayout;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.charge.viewmodels.ChargeImport;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        named = "Import manager for charges",
        bookmarking = BookmarkPolicy.AS_ROOT
)
@ViewModelLayout(paged = -1)
public class ChargeImportManager extends EstatioViewModel {

    public String title() {
        return "Import manager for Charges";
    }

    public ChargeImportManager() {
        this.name = "Charge Import";
    }

    //region > name (property)
    private String name;

    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    //endregion

    @Action
    @ActionLayout(named = "Import", cssClassFa = "fa-upload")
    @CollectionLayout(paged = -1)
    public List<ChargeImport> importBlob(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<ChargeImport> lineItems =
                excelService.fromExcel(spreadsheet, ChargeImport.class);
        return lineItems;
    }

    @Inject
    private DomainObjectContainer container;

    @Inject
    private ExcelService excelService;

}
