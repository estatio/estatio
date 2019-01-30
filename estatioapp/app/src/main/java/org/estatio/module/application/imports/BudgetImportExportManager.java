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
package org.estatio.module.application.imports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModelLayout;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.budgetassignment.imports.DirectCostLine;
import org.estatio.module.budgetassignment.imports.KeyItemImportExportLineItem;
import org.estatio.module.budgetassignment.imports.PartitioningTableItemImportExportService;
import org.estatio.module.charge.imports.ChargeImport;

import lombok.Getter;
import lombok.Setter;

// TODO: need to untangle this and push back down to budget module
@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.BudgetImportExportManager"
)
@DomainObjectLayout(
        named = "Import Export manager for budget",
        bookmarking = BookmarkPolicy.AS_ROOT
)
@ViewModelLayout()
public class BudgetImportExportManager {

    public String title() {
        return "Import / Export manager for budget";
    }

    public BudgetImportExportManager() {
        this.name = "Budget Import / Export";
        this.fileName = "export.xlsx";
    }

    public BudgetImportExportManager(Budget budget) {
        this();
        this.budget = budget;
    }

    public BudgetImportExportManager(BudgetImportExportManager budgetImportExportManager) {
        this.fileName = budgetImportExportManager.getFileName();
        this.name = budgetImportExportManager.getName();
        this.budget = budgetImportExportManager.getBudget();
    }

    @Getter @Setter
    private String name;
    @Getter @Setter
    private Budget budget;
    @Getter @Setter
    private String fileName;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Change File Name")
    @MemberOrder(name = "fileName", sequence = "1")
    public BudgetImportExportManager changeFileName(final String fileName) {
        this.setFileName(fileName);
        return new BudgetImportExportManager(this);
    }

    public String default0ChangeFileName() {
        return getFileName();
    }

    @SuppressWarnings("unchecked")
    @Collection()
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    public List<BudgetImportExport> getLines() {
        return budgetImportExportService.lines(this);
    }

    public List<KeyItemImportExportLineItem> getKeyItemLines() {
        List<KeyItemImportExportLineItem> result = new ArrayList<>();
        if (getBudget()==null){return result;} // for import from menu where budget unknown
        for (KeyTable keyTable : this.getBudget().getKeyTables()){
            result.addAll(partitioningTableItemImportExportService.items(keyTable.getItems()));
        }
        return result;
    }

    public List<DirectCostLine> getDirectCostLines() {
        List<DirectCostLine> result = new ArrayList<>();
        if (getBudget()==null){return result;} // for import from menu where budget unknown
        for (DirectCostTable directCostTable : this.getBudget().getDirectCostTables()){
            result.addAll(partitioningTableItemImportExportService.directCosts(directCostTable.getItems()));
        }
        return result;
    }

    public List<ChargeImport> getCharges() {
        List<ChargeImport> result = new ArrayList<>();
        if (getBudget()==null){return result;} // for import from menu where budget unknown
        return budgetImportExportService.charges(this);
    }


    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout()
    @CollectionLayout()
    public Budget importBudget(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        return budgetImportExportService.importBudget(getBudget(), spreadsheet);
    }

    public String disableImportBudget(){
        if (getBudget()==null || getBudget().getStatus()!=Status.NEW) return "This budget is assigned already";
        return null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob exportBudget() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        WorksheetSpec spec1 = new WorksheetSpec(BudgetImportExport.class, "budget");
        WorksheetSpec spec2 = new WorksheetSpec(KeyItemImportExportLineItem.class, "keyItems");
        WorksheetSpec spec3 = new WorksheetSpec(DirectCostLine.class, "directCosts");
        WorksheetSpec spec4 = new WorksheetSpec(ChargeImport.class, "charges");
        WorksheetContent worksheetContent = new WorksheetContent(getLines(), spec1);
        WorksheetContent keyItemsContent = new WorksheetContent(getKeyItemLines(), spec2);
        WorksheetContent directCostsContent = new WorksheetContent(getDirectCostLines(), spec3);
        WorksheetContent chargesContent = new WorksheetContent(getCharges(), spec4);
        return excelService.toExcel(Arrays.asList(worksheetContent, keyItemsContent, directCostsContent, chargesContent), fileName);

    }

    public String disableExportBudget() {
        return getFileName() == null ? "file name is required" : null;
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }
    
    @Inject
    private ExcelService excelService;

    @Inject
    private BudgetImportExportService budgetImportExportService;

    @Inject
    private PartitioningTableItemImportExportService partitioningTableItemImportExportService;
    
    @Inject
    PartitioningTableRepository partitioningTableRepository;

    @Inject
    private ServiceRegistry2 serviceRegistry2;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject FactoryService factoryService;

    @Inject MessageService messageService;

    @Inject BudgetCalculationRepository budgetCalculationRepository;

}
