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
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budgetassignment.imports.DirectCostLine;
import org.estatio.module.budgetassignment.imports.PartitioningTableItemImportExportService;

@Mixin(method="act")
public class DirectCostTable_export {

    private final DirectCostTable directCostTable;

    public DirectCostTable_export(final DirectCostTable directCostTable) {
        this.directCostTable = directCostTable;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "5")
    public Blob act(final String filename) {
        final String fileNameToUse = withExtension(filename, ".xlsx");
        WorksheetSpec spec = new WorksheetSpec(DirectCostLine.class, "directCosts");
        WorksheetContent worksheetContent = new WorksheetContent(partitioningTableItemImportExportService.directCostsToLines(directCostTable.getItems()), spec);
        return excelService.toExcel(worksheetContent, fileNameToUse);

    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }

    @Inject ExcelService excelService;

    @Inject PartitioningTableItemImportExportService partitioningTableItemImportExportService;

}
