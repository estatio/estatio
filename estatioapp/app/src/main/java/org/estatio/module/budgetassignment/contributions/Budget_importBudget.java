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
package org.estatio.module.budgetassignment.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budgetassignment.imports.BudgetImportExportService;

@Mixin(method = "act")
public class Budget_importBudget {

    private final Budget budget;

    public Budget_importBudget(Budget budget) {
        this.budget = budget;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Budget act(final Blob spreadSheet,
            final boolean importKeyTables,
            final boolean importCharges) {
        return budgetImportExportService.importBudget(budget, spreadSheet, importKeyTables, importCharges);
    }

    @Inject
    private ExcelService excelService;

    @Inject
    private BudgetImportExportService budgetImportExportService;

}
