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
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.viewmodels.KeyItemImportExportLineItem;
import org.estatio.dom.budgeting.viewmodels.Status;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        named = "Import/export manager for key item",
        bookmarking = BookmarkPolicy.AS_ROOT
)
@MemberGroupLayout(left = {"File", "Criteria"})
public class KeyItemImportExportManager extends EstatioViewModel {

    public KeyItemImportExportManager(KeyItemImportExportManager keyItemImportExportManager) {
        this.keyTable = keyItemImportExportManager.getKeyTable();
        this.fileName = keyItemImportExportManager.getFileName();
    }

    // //////////////////////////////////////

    public String title() {
        return "Import export key items";
    }

    public KeyItemImportExportManager() {
    }

    public KeyItemImportExportManager(final KeyTable keyTable) {
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
    @ActionLayout(named = "Change File Name")
    @MemberOrder(name = "fileName", sequence = "1")
    public KeyItemImportExportManager changeFileName(final String fileName) {
        this.setFileName(fileName);
        return new KeyItemImportExportManager(this);
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
    public List<KeyItemImportExportLineItem> getKeyItems() {
        return keyItemImportExportService.items(this);
    }

    // //////////////////////////////////////
    // export (action)
    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    @MemberOrder(name = "keyItems", sequence = "1")
    public Blob export() {
        final String fileName = withExtension(getFileName(), ".xlsx");
        return excelService.toExcel(getKeyItems(), KeyItemImportExportLineItem.class, fileName);
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

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Import", cssClassFa = "fa-upload")
    @MemberOrder(name = "keyItems", sequence = "2")
    public List<KeyItemImportExportLineItem> importBlob(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        List<KeyItemImportExportLineItem> lineItems =
                excelService.fromExcel(spreadsheet, KeyItemImportExportLineItem.class);
        container.informUser(lineItems.size() + " items imported");

        List<KeyItemImportExportLineItem> newItems = new ArrayList<>();
        for (KeyItemImportExportLineItem item : lineItems) {
            item.validate();
            newItems.add(new KeyItemImportExportLineItem(item));
        }
        for (KeyItem keyItem : keyTable.getItems()) {
            Boolean keyItemFound = false;
            for (KeyItemImportExportLineItem lineItem : newItems){
                if (lineItem.getUnitReference().equals(keyItem.getUnit().getReference())){
                    keyItemFound = true;
                    break;
                }
            }
            if (!keyItemFound) {
                KeyItemImportExportLineItem deletedItem = new KeyItemImportExportLineItem(keyItem);
                deletedItem.setStatus(Status.DELETED);
                newItems.add(deletedItem);
            }
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
    private KeyItemImportExportService keyItemImportExportService;

    @Inject
    private KeyTableRepository keyTableRepository;

}
