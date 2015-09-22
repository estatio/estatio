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
package org.estatio.app.budget;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        named = "Import/export manager",
        bookmarking = BookmarkPolicy.AS_ROOT
)
@MemberGroupLayout(left = {"File", "Criteria"})
public class BudgetKeyItemImportExportManager extends EstatioViewModel {

    // //////////////////////////////////////

    public String title() {
        return "Import/export manager";
    }

    public BudgetKeyItemImportExportManager() {
    }

    public BudgetKeyItemImportExportManager(final KeyTable keyTable) {
        this.keyTable = keyTable;
        this.fileName = "export.xlsx";
    }

    private KeyTable keyTable;

    public KeyTable getKeyTable() {
        return keyTable;
    }

    public void setKeyTable(final KeyTable keyTable) {
        this.keyTable = keyTable;
    }

    // //////////////////////////////////////
    // fileName (property)
    // changeFileName
    // //////////////////////////////////////

    private String fileName;

    @MemberOrder(name = "File", sequence = "1")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Change")
    @MemberOrder(name = "fileName", sequence = "1")
    public BudgetKeyItemImportExportManager changeFileName(final String fileName) {
        setFileName(fileName);
        return this;
    }

    public String default0ChangeFileName() {
        return getFileName();
    }

    // //////////////////////////////////////
    // allBudgetKeyItems
    // //////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Collection
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    public List<BudgetKeyItemImportExportLineItem> getBudgetKeyItems() {
        return budgetKeyItemImportExportService.items(this);
    }

    // //////////////////////////////////////
    // export (action)
    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(name = "budgetKeyItems", sequence = "1")
    public Blob export() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        return excelService.toExcel(getBudgetKeyItems(), BudgetKeyItemImportExportLineItem.class, fileName);
    }

    public String disableExport() {
        return getFileName() == null ? "file name is required" : null;
    }

    private static String withExtension(final String fileName, final String fileExtension) {
        return fileName.endsWith(fileExtension) ? fileName : fileName + fileExtension;
    }

    // //////////////////////////////////////
    // import (action)
    // //////////////////////////////////////

    @Action
    @ActionLayout(named = "Import")
    @MemberOrder(name = "budgetKeyItems", sequence = "2")
    public List<BudgetKeyItemImportExportLineItem> importBlob(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<BudgetKeyItemImportExportLineItem> lineItems =
                excelService.fromExcel(spreadsheet, BudgetKeyItemImportExportLineItem.class);
        container.informUser(lineItems.size() + " items imported");

        List<BudgetKeyItemImportExportLineItem> newItems = new ArrayList<>();
        for (BudgetKeyItemImportExportLineItem item : lineItems) {
            //            item.setKeyTable(getKeyTable());
            // yodo: doesn't work; used trick by changing keyTable to String budgetKeyTableName on BudgetKeyItemImportExportLineItem
            item.validate();
            newItems.add(new BudgetKeyItemImportExportLineItem(item));
        }
        return newItems;
    }


    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @javax.inject.Inject
    private DomainObjectContainer container;

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private BudgetKeyItemImportExportService budgetKeyItemImportExportService;

    @Inject
    private KeyTables keyTables;

}
