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

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budgetassignment.imports.BudgetImportExportService;
import org.estatio.module.budgetassignment.imports.BudgetItemImportExport;
import org.estatio.module.budgetassignment.imports.BudgetPartitionItemImportExport;
import org.estatio.module.budgetassignment.imports.DirectCostLine;
import org.estatio.module.budgetassignment.imports.KeyItemImportExportLine;
import org.estatio.module.charge.imports.ChargeImport;

@Mixin(method = "act")
public class Budget_exportBudget {

    private final Budget budget;

    public Budget_exportBudget(Budget budget) {
        this.budget = budget;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob act(final String filename) {
        final String fileNameToUse = withExtension(filename, ".xlsx");
        WorksheetSpec spec1 = new WorksheetSpec(BudgetItemImportExport.class, "budgetItems");
        WorksheetSpec spec2 = new WorksheetSpec(BudgetPartitionItemImportExport.class, "partitionItems");
        WorksheetSpec spec3 = new WorksheetSpec(KeyItemImportExportLine.class, "keyItems");
        WorksheetSpec spec4 = new WorksheetSpec(DirectCostLine.class, "directCosts");
        WorksheetSpec spec5 = new WorksheetSpec(ChargeImport.class, "charges");
        WorksheetContent budgetItemsContent = new WorksheetContent(budgetImportExportService.getBudgetItemLines(budget), spec1);
        WorksheetContent partitionItemsContent = new WorksheetContent(budgetImportExportService.getPartionItemLines(budget), spec2);
        WorksheetContent keyItemsContent = new WorksheetContent(budgetImportExportService.getKeyItemLines(budget), spec3);
        WorksheetContent directCostsContent = new WorksheetContent(budgetImportExportService.getDirectCostLines(budget), spec4);
        WorksheetContent chargesContent = new WorksheetContent(budgetImportExportService.getCharges(budget), spec5);
        return excelService.toExcel(
                Arrays.asList(budgetItemsContent, partitionItemsContent, keyItemsContent, directCostsContent, chargesContent), fileNameToUse);
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }

    @Inject
    private ExcelService excelService;

    @Inject
    private BudgetImportExportService budgetImportExportService;

}
