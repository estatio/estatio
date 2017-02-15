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
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.ChargeImportManager"
)
@DomainObjectLayout(paged = -1)
public class ChargeImportManager {

    //region > constructors, title
    public ChargeImportManager() {
        this.name = "Charge Import";
    }

    public String title() {
        return "Import manager for Charges";
    }

    //endregion


    @Getter @Setter
    private String name;


    //region > importBlob (action)
    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @CollectionLayout(paged = -1)
    public List<ChargeImport> importBlob(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<ChargeImport> lineItems =
                excelService.fromExcel(spreadsheet, ChargeImport.class, ChargeImport.class.getSimpleName());
        return lineItems;
    }
    //endregion


    //region > injected services
    @Inject
    private DomainObjectContainer container;

    @Inject
    private ExcelService excelService;

    //endregion

}
