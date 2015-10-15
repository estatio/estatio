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


//import org.apache.isis.applib.annotation.*;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.actinvoc.ActionInvocationContext;
import org.isisaddons.module.excel.dom.ExcelService;

import javax.annotation.PostConstruct;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetSpreadsheetImportMenu {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )

    @MemberOrder(sequence = "1")
    public ChargeImportManager uploadCharges() {

        return new ChargeImportManager();

    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @MemberOrder(sequence = "2")
    public KeyTablesImportManager uploadKeyTables() {

        return new KeyTablesImportManager();

    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @MemberOrder(sequence = "3")
    public BudgetImportManager uploadBudget() {

        return new BudgetImportManager();

    }


    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private ActionInvocationContext actionInvocationContext;

}
