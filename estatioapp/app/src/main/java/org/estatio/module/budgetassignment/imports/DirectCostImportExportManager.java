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
package org.estatio.module.budgetassignment.imports;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keytable.DirectCostTable;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.DirectCostImportExportManager"
)
public class DirectCostImportExportManager {

    public DirectCostImportExportManager() {
    }

    public DirectCostImportExportManager(DirectCostImportExportManager directCostImportExportManager) {
        this.directCostTable = directCostImportExportManager.getDirectCostTable();
        this.fileName = directCostImportExportManager.getFileName();
    }

    public DirectCostImportExportManager(final DirectCostTable directCostTable) {
        this.directCostTable = directCostTable;
        this.fileName = directCostTable.getName().concat(" - ").concat("export.xlsx");
    }

    public String title() {
        return "Import export direct costs";
    }

    @Getter @Setter
    private DirectCostTable directCostTable;

    @Getter @Setter
    private String fileName;

    //region > changeFileName (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Change File Name")
    @MemberOrder(name = "fileName", sequence = "1")
    public DirectCostImportExportManager changeFileName(final String fileName) {
        this.setFileName(fileName);
        return new DirectCostImportExportManager(this);
    }

    public String default0ChangeFileName() {
        return getFileName();
    }

    //endregion


    @SuppressWarnings("unchecked")
    @Collection
    public List<DirectCostLine> getDirectCosts() {
        return keyItemImportExportService.items(this);
    }

    //region > export (action)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    @MemberOrder(name = "directCosts", sequence = "1")
    public Blob export() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        WorksheetSpec spec = new WorksheetSpec(DirectCostLine.class, "directCosts");
        WorksheetContent worksheetContent = new WorksheetContent(getDirectCosts(), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public String disableExport() {
        return getFileName() == null ? "file name is required" : null;
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }
    //endregion

    //region > import (action)

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Import", cssClassFa = "fa-upload")
    @MemberOrder(name = "directCosts", sequence = "2")
    public List<DirectCostLine> importBlob(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        WorksheetSpec spec = new WorksheetSpec(DirectCostLine.class, "directCosts");
        List<DirectCostLine> lineItems =
                excelService.fromExcel(spreadsheet, spec);
        messageService.informUser(lineItems.size() + " items imported");

        if (getDirectCostTable().getBudget().getStatus()== org.estatio.module.budget.dom.budget.Status.NEW) {
            List<DirectCostLine> importedLines = new ArrayList<>();
            for (DirectCostLine item : lineItems) {
                item.validate();
                importedLines.add(new DirectCostLine(item));
            }
            for (DirectCost directCost : directCostTable.getItems()) {
                Boolean directCostFound = false;
                for (DirectCostLine lineItem : importedLines) {
                    if (lineItem.getUnitReference().equals(directCost.getUnit().getReference())) {
                        directCostFound = true;
                        break;
                    }
                }
                if (!directCostFound) {
                    DirectCostLine deletedItem = new DirectCostLine(directCost, null);
                    deletedItem.setStatus(Status.DELETED);
                    importedLines.add(deletedItem);
                }
            }
            return importedLines;

        } else {

            if (getDirectCostTable().getBudget().getStatus()== org.estatio.module.budget.dom.budget.Status.ASSIGNED) {
                // only update actuals
                messageService.informUser("Since the budget is assigned, only audited values can be updated");
                List<DirectCostLine> updatedLines = new ArrayList<>();
                for (DirectCostLine item : lineItems) {
                    item.validate();
                    if (item.getStatus()==Status.UPDATED) {
                        updatedLines.add(new DirectCostLine(item));
                    }
                }
                return updatedLines;
            }

        }

        return null;

    }

    public String disableImportBlob(){
        if (getDirectCostTable().getBudget().getStatus()== org.estatio.module.budget.dom.budget.Status.RECONCILED) return "The budget is reconciled already";
        return null;
    }

    @Inject
    private MessageService messageService;

    @Inject
    private ExcelService excelService;

    @Inject
    private KeyItemImportExportService keyItemImportExportService;

}
