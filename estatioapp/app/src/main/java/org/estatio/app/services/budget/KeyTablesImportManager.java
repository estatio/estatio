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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.value.Blob;
import org.estatio.app.EstatioViewModel;
import org.estatio.app.services.budget.viewmodels.KeyTableImport;
import org.isisaddons.module.excel.dom.ExcelService;

import javax.inject.Inject;
import java.util.List;

//import org.apache.isis.applib.annotation.*;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        named = "Import manager",
        bookmarking = BookmarkPolicy.AS_ROOT
)
@MemberGroupLayout(left = {"File", "Criteria"})
public class KeyTablesImportManager extends EstatioViewModel {

    public String title() {
        return "Import manager for Key Tables";
    }

    public KeyTablesImportManager() {
        this.name = "Key Tables Import";
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



    // //////////////////////////////////////
    // import (action)
    // //////////////////////////////////////

    @Action
    @ActionLayout(named = "Import Key Tables", cssClassFa = "fa-upload")
    public List<KeyTableImport> importBlob(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<KeyTableImport> lineItems =
                excelService.fromExcel(spreadsheet, KeyTableImport.class);
        container.informUser(lineItems.size() + " items imported");
        return lineItems;
    }


    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @Inject
    private DomainObjectContainer container;

    @Inject
    private ExcelService excelService;

}
