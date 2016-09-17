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
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModelLayout;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL
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
            result.addAll(keyItemImportExportService.items(keyTable.getItems()));
        }
        return result;
    }


    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout()
    @CollectionLayout()
    public Budget importBudget(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {

        Budget importedBudget = new Budget();
        WorksheetSpec spec1 = new WorksheetSpec(BudgetImportExport.class, "budget");
        WorksheetSpec spec2 = new WorksheetSpec(KeyItemImportExportLineItem.class, "keyItems");
        List<List<?>> objects =
                excelService.fromExcel(spreadsheet, Arrays.asList(spec1, spec2));
        List<BudgetImportExport> lineItems = (List<BudgetImportExport>) objects.get(0);
        for (BudgetImportExport lineItem :lineItems){
            importedBudget = (Budget) lineItem.importData(null).get(0);
        }
        setBudget(importedBudget);

        List<KeyTable> keyTablesToImport = keyTablesToImport(lineItems);
        List<KeyItemImportExportLineItem> keyItemLines = (List<KeyItemImportExportLineItem>) objects.get(1);

        // filter case where no key items are filled in
        if (keyItemLines.size() == 0) {return getBudget();}

        for (KeyTable keyTable : keyTablesToImport){
            List<KeyItemImportExportLineItem> itemsToImportForKeyTable = new ArrayList<>();
            for (KeyItemImportExportLineItem keyItemLine : keyItemLines){
                if (keyItemLine.getKeyTableName().equals(keyTable.getName())){
                    itemsToImportForKeyTable.add(new KeyItemImportExportLineItem(keyItemLine));
                }
            }
            for (KeyItem keyItem : keyTable.getItems()) {
                Boolean keyItemFound = false;
                for (KeyItemImportExportLineItem lineItem : itemsToImportForKeyTable){
                    if (lineItem.getUnitReference().equals(keyItem.getUnit().getReference())){
                        keyItemFound = true;
                        break;
                    }
                }
                if (!keyItemFound) {
                    KeyItemImportExportLineItem deletedItem = new KeyItemImportExportLineItem(keyItem);
                    deletedItem.setStatus(Status.DELETED);
                    itemsToImportForKeyTable.add(deletedItem);
                }
            }
            for (KeyItemImportExportLineItem item : itemsToImportForKeyTable){
                serviceRegistry2.injectServicesInto(item);
                item.validate();
                item.apply();
            }
        }
        return getBudget();
    }

    private List<KeyTable> keyTablesToImport(final List<BudgetImportExport> lineItems){
        List<KeyTable> result = new ArrayList<>();
        for (BudgetImportExport lineItem :lineItems) {
            KeyTable foundKeyTable = keyTableRepository.findByBudgetAndName(getBudget(), lineItem.getKeyTableName());
            if (!result.contains(foundKeyTable)) {
                result.add(foundKeyTable);
            }
        }
        return result;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob exportBudget() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        WorksheetSpec spec1 = new WorksheetSpec(BudgetImportExport.class, "budget");
        WorksheetSpec spec2 = new WorksheetSpec(KeyItemImportExportLineItem.class, "keyItems");
        WorksheetContent worksheetContent = new WorksheetContent(getLines(), spec1);
        WorksheetContent keyItemsContent = new WorksheetContent(getKeyItemLines(), spec2);
        return excelService.toExcel(Arrays.asList(worksheetContent, keyItemsContent), fileName);

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
    private KeyItemImportExportService keyItemImportExportService;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
